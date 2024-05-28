package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.dto.SkillDto;
import com.communityHubSystem.communityHub.dto.SkillDtoForShow;
import com.communityHubSystem.communityHub.dto.UserDTO;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.Policy;
import com.communityHubSystem.communityHub.models.Skill;
import com.communityHubSystem.communityHub.models.User;
import com.communityHubSystem.communityHub.models.User_Skill;
import com.communityHubSystem.communityHub.repositories.*;
import com.communityHubSystem.communityHub.services.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final PostService postService;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final PasswordEncoder passwordEncoder;
    private final User_SkillRepository userSkillRepository;
    private final User_ChatRoomRepository user_ChatRoomRepository;
    private final PolicyRepository policyRepository;
    private final ExcelUploadService excelUploadService;
    private final User_SkillService user_skillService;
    private final SkillService skillService;
    private final PolicyService policyService;

    @GetMapping("/getAllSkills")
    public ResponseEntity<List<String>> getAllSkills() {
        List<Skill> skills = skillRepository.findAll();
        List<String> skillNames = skills.stream().map(Skill::getName).collect(Collectors.toList());
        return ResponseEntity.ok(skillNames);
    }
    @GetMapping("/allUser")
    @ResponseBody
    public ResponseEntity<List<User>> giveAllUsers() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    @PutMapping("/update")
    @ResponseBody
    public ResponseEntity<?> update(@RequestBody User user) {
        userService.updateUserData(user);
        return ResponseEntity.status(HttpStatus.OK).body("Ok");
    }

    @PostMapping("/search")
    @ResponseBody
    public ResponseEntity<List<User>> searchMethod(@RequestBody UserDTO userDTO) {
        System.err.println(userDTO);
        userDTO.isActive();
        return ResponseEntity.ok(userService.searchMethod(userDTO));
    }

    @GetMapping("/profile")
    public String profilePage(Model model) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        var user = userService.findByStaffId(staffId).orElseThrow();
        Set<User_Skill> user_skills=user.getUser_skills();
        System.out.println(user);
        System.out.println(user.getPosts().size());
        model.addAttribute("user", user);
        model.addAttribute("userSkills", user_skills);
        return "/user/user-profile";
    }


    @PostMapping("/upload-data")
    @ResponseBody
    public ResponseEntity<?> uploadUsersData(@RequestParam("file") MultipartFile file) throws IOException {
        this.excelUploadService.uploadEmployeeData(file);
        return ResponseEntity
                .ok(Map.of("Message", " Employee data uploaded and saved to database successfully"));
    }

    @GetMapping("/View-all-users")
    public String viewUser(Model model) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> optionalUser = userService.findByStaffId(staffId);
        if (optionalUser.isPresent()) {
            User loggedInUser = optionalUser.get();
            List<User> users = userRepository.findAll();
            List<User> filteredUsers = users.stream()
                    .filter(user -> !user.getStaffId().equals(loggedInUser.getStaffId()))
                    .collect(Collectors.toList());
            model.addAttribute("users", filteredUsers);
        } else {
            model.addAttribute("users", Collections.emptyList());
        }
        return "/user/view-all-user";
    }

    @GetMapping("/getSkills")
    @ResponseBody
    public ResponseEntity<List<SkillDtoForShow>> getSkills() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> optionalUser = userService.findByStaffId(staffId);
        if (optionalUser.isPresent()) {
            List<User_Skill> user_skills = userSkillRepository.findByUserId(optionalUser.get().getId());
            List<SkillDtoForShow> skillsWithExperience = new ArrayList<>();

            for (User_Skill user_skill : user_skills) {
                var skill = user_skill.getSkill();
                if (skill != null) {
                    SkillDtoForShow dto = new SkillDtoForShow(skill.getId(), skill.getName(), user_skill.getExperience());
                    skillsWithExperience.add(dto);
                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(skillsWithExperience);
        }
        // If user not found or no skills associated, return an empty list
        return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
    }
    @PostMapping("/savePassword")
    public ResponseEntity<Map<String, String>> savePassword(@RequestBody Map<String, String> requestBody) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> optionalUser = userService.findByStaffId(staffId);
        if (optionalUser.isPresent()) {
            System.out.println("user present");
            User user = optionalUser.get();
            String password = requestBody.get("password");
            String phoneNumber = requestBody.get("phoneNumber");
            String dob = requestBody.get("dob");
            String gender = requestBody.get("gender");
            String hobbies = requestBody.get("hobbies");
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
            user.setPhone(phoneNumber);
            user.setDob(dob);
            user.setGender(gender);
            user.setHobby(hobbies);
            user.setRole(User.Role.USER);
            userRepository.save(user);

        }
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password saved");
        return ResponseEntity.ok(response);
    }


    @PostMapping("/saveSkill")
    public ResponseEntity<String> saveSkill(@RequestBody Map<String, String> requestBody) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> optionalUser = userService.findByStaffId(staffId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // Get selected skills string and experience from the request body
            String selectedSkillsString = requestBody.get("selectedSkills");
            String experiencesString = requestBody.get("experience");
            System.out.println(selectedSkillsString);
            System.out.println(experiencesString);

            // Split the selected skills string into an array of skill names
            String[] selectedSkillsArray = (selectedSkillsString != null) ? selectedSkillsString.split(",") : new String[0];
            String[] experiencesArray = (experiencesString != null) ? experiencesString.split(",") : new String[0];

            // Iterate through the skill names
            for (int i = 0; i < selectedSkillsArray.length; i++) {
                String skillName = selectedSkillsArray[i];
                String experience = experiencesArray.length > i ? experiencesArray[i] : "0";

                // Check if the skill name already exists in the database
                Optional<Skill> existingSkill = skillRepository.findByName(skillName);

                Skill skill;
                if (existingSkill.isPresent()) {
                    // Skill exists, use the existing skill
                    skill = existingSkill.get();
                } else {
                    // Skill doesn't exist, create a new skill
                    skill = new Skill();
                    skill.setName(skillName);
                    // Save the new skill to the database
                    skill = skillRepository.save(skill);
                }

                Optional<User_Skill> existingUserSkill = userSkillRepository.findByUserIdAndSkillId(user.getId(), skill.getId());
                User_Skill userSkill;
                if (existingUserSkill.isPresent()) {
                    userSkill = existingUserSkill.get();
                    userSkill.setExperience(experience); // Update experience
                } else {
                    userSkill = new User_Skill();
                    userSkill.setUser(user);
                    userSkill.setSkill(skill);
                    userSkill.setExperience(experience);
                }

                userSkillRepository.save(userSkill);
            }

            // Save the updated user entity
            return ResponseEntity.ok("Skills saved");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @PostMapping("/updateProfilePhoto")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateProfilePhoto(@ModelAttribute UserDTO userDTO, HttpSession session) throws IOException {
        var user = userService.updateProfilePhoto(userDTO.getFile());
        Map<String, String> response = new HashMap<>();
        response.put("photo", user.getPhoto());
        session.removeAttribute("user");
        session.setAttribute("user", user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/saveImage")
    public ResponseEntity<?> saveImage(@RequestBody byte[] image) throws IOException {
        System.out.println("Image file name: " + Arrays.toString(image));
        var ips = new ByteArrayInputStream(image);
        var photo = new MockMultipartFile(Arrays.toString(image), ips);
        userService.saveImage(photo);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Image saved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/save-policy")
    public ResponseEntity<String> savePolicy(@RequestBody Map<String, Object> request) {
        String policyContent = (String) request.get("policyContent");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String staffId = authentication.getName();
        Optional<User> user = userRepository.findByStaffId(staffId);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"success\": false}");
        }
        Policy policy = new Policy();
        policy.setRule(policyContent);
        policy.setDate(new Date());


        policy.setUser(user.get());

        policyRepository.save(policy);

        return ResponseEntity.ok().body("{\"success\": true}");
    }

    @GetMapping("/getAllPolicies")
    @ResponseBody
    public ResponseEntity<List<Policy>> getAllPolicies() {
        return ResponseEntity.ok(policyRepository.findAll());
    }

    @GetMapping("/policy")
    public String policyPage() {
        return "/user/policy";
    }

    @GetMapping("/getOnePolicy")
    @ResponseBody
    public ResponseEntity<Policy> getOnePolicy(@RequestParam Long policyId) {
        Optional<Policy> policyOptional = policyRepository.findById(policyId);
        return policyOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/updatePolicy")
    @ResponseBody
    public ResponseEntity<String> updatePolicy(@RequestBody Policy policy) {
        policyRepository.save(policy);
        return ResponseEntity.ok("Policy updated successfully.");
    }

    @DeleteMapping("/deletePolicy/{policyId}")
    @ResponseBody
    public ResponseEntity<String> deletePolicy(@PathVariable("policyId") Long policyId) {
        policyRepository.deleteById(policyId);
        return ResponseEntity.ok("Policy deleted successfully.");
    }

    @PostMapping("/updateUserStatus")
    @ResponseBody
    public ResponseEntity<String> banUserStatus(@RequestBody Map<String, Object> requestBody) {
        Long userId = Long.parseLong(requestBody.get("userId").toString());
        boolean isActive = (boolean) requestBody.getOrDefault("isActive", false);
        String banReason = (String) requestBody.get("banReason");
        userService.updateUserStatus(userId, isActive, banReason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/removedAdmin")
    public ResponseEntity<String> updateAdminStatus(@RequestBody Map<String, Object> requestBody) {
        Long userId = Long.parseLong(requestBody.get("userId").toString());
        boolean isRemoved = true;
        String removedReason = (String) requestBody.get("removedReason");
        userService.updateAdminToUserStatus(userId, isRemoved, removedReason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/updateUserToAdminStatus") // Adjusted URL mapping
    public ResponseEntity<String> updateUserStatus(@RequestBody Map<String, Object> requestBody) {
        Long userId = Long.parseLong(requestBody.get("userId").toString());
        boolean pending = (boolean) requestBody.getOrDefault("pending", false);
        boolean done = (boolean) requestBody.getOrDefault("done", false);
        boolean removed = (boolean) requestBody.getOrDefault("removed", false);
        userService.updateUserToAdminStatus(userId, pending, done, removed);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rejectAdminRole")
    public ResponseEntity<String> rejectAdminStatus(@RequestBody Map<String, Object> requestBody) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String staffId = authentication.getName();
        Optional<User> user = userRepository.findByStaffId(staffId);
        if (user.isPresent()) {
            long userId = user.get().getId();
            boolean pending = false;
            boolean done = false;
            boolean removed = false;
            boolean reject = true;
            String rejectReason = (String) requestBody.get("rejectedReason");
            userService.rejectAdminRole(userId, pending, done, removed, reject, rejectReason);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }


    @PostMapping("/acceptRejected")
    public ResponseEntity<Map<String, Object>> acceptReject(@RequestBody Map<String, Long> requestBody) {
        Long userId = requestBody.get("userId");
        userService.acceptReject(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/updateUserStatusAndRole")
    public ResponseEntity<String> updateUserStatusAndRole(@RequestBody Map<String, Boolean> statusMap) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String staffId = authentication.getName();
        Optional<User> user = userRepository.findByStaffId(staffId);
        if (user.isPresent()) {
            long userId = user.get().getId();
            boolean pending = statusMap.getOrDefault("pending", false);
            boolean done = statusMap.getOrDefault("done", false);
            boolean removed = statusMap.getOrDefault("removed", false);
            userService.updateUserToAdminStatus(userId, pending, done, removed);
            if (!pending && done) {
                userService.updateUserRoleToAdmin(userId);
            }
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @PostMapping("/getRejectReason")
    public ResponseEntity<Map<String, Object>> getRejectReason(@RequestBody Map<String, Long> requestBody) {
        Long userId = requestBody.get("userId");
        User user = userRepository.findById(userId).orElseThrow();
        Map<String, Object> response = new HashMap<>();
        response.put("rejectReason", user.getRejectReason());
        response.put("name", user.getName());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/checkBannedStatus")
    public ResponseEntity<Map<String, Object>> checkBannedStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> userOptional = userRepository.findByStaffId(staffId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Boolean banned = user.isActive();
            String bannedReason = user.getBannedReason();
            String name = user.getName();
            Map<String, Object> response = new HashMap<>();
            response.put("active", banned);
            response.put("bannedReason", bannedReason);
            response.put("name", name);
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/checkPendingStatus")
    public ResponseEntity<Map<String, Object>> checkPendingStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> userOptional = userRepository.findByStaffId(staffId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Boolean pending = user.isPending();
            Boolean removed = user.isRemoved();
            String removedReason = user.getRemovedReason();
            String name = user.getName();
            Map<String, Object> response = new HashMap<>();
            response.put("pending", pending);
            response.put("removed", removed);
            response.put("removedReason", removedReason);
            response.put("name", name);
            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/updateProfile")
    public ResponseEntity<String> updateProfile(@RequestBody Map<String, Object> requestBody) {
        String staffId = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> optionalUser = userService.findByStaffId(staffId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPhone((String) requestBody.get("phoneNumber"));
            user.setGender((String) requestBody.get("gender"));

            // Convert hobbies list to comma-separated string
            List<String> hobbies = (List<String>) requestBody.get("hobbies");
            String selectedHobbies = String.join(",", hobbies);
            user.setHobby(selectedHobbies);

            String selectedSkillsString = (String) requestBody.get("selectedSkills");
            String experience = (String) requestBody.get("experience");

            if (selectedSkillsString != null) {
                String[] selectedSkillsArray = selectedSkillsString.split(",");

                for (String skillName : selectedSkillsArray) {
                    // Check if the skill name already has this skill
                    Optional<Skill> existingSkill = skillRepository.findByName(skillName);

                    Skill skill;

                    if (existingSkill.isPresent()) {
                        skill = existingSkill.get();
                    } else {
                        skill = new Skill();
                        skill.setName(skillName);
                        skill = skillRepository.save(skill);
                    }

                    // Check if the user already has this skill
                    Optional<User_Skill> existingUserSkill = userSkillRepository.findByUserIdAndSkillId(user.getId(), skill.getId());
                    User_Skill userSkill;
                    if (existingUserSkill.isPresent()) {
                        userSkill = existingUserSkill.get();
                    } else {
                        userSkill = new User_Skill();
                        userSkill.setUser(user);
                        userSkill.setSkill(skill);
                        userSkill.setExperience(experience);
                        userSkillRepository.save(userSkill);
                    }
                }
            }

            userRepository.save(user);

            return ResponseEntity.ok("User profile updated successfully");
        } else {
            return ResponseEntity.badRequest().body("User not found");
        }
    }

    @GetMapping("/userSearchMethod/{input}")
    @ResponseBody
    public ResponseEntity<List<User>> userSearchMethod(@PathVariable("input") String input) throws UnsupportedEncodingException {
        return ResponseEntity.ok(userService.userSearchMethod(input));
    }

    @GetMapping("/other-user-profile")
    public String getUserProfile(@RequestParam("id") Long userId, Model model) {
        User user = userService.findById(userId);
        Set<User_Skill>user_skills=user.getUser_skills();
        model.addAttribute("user", user);
        model.addAttribute("userSkills",user_skills);
        return "/user/other-user-profile";
    }


    @GetMapping("/getHobbies")
    public ResponseEntity<?> getUserHobbies() {
        String staffId = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> optionalUser = userService.findByStaffId(staffId);
        if (optionalUser.isPresent()) {
            String userHobbies = optionalUser.get().getHobby();
            System.out.println(userHobbies);
            Map<String, Object> response = new HashMap<>();
            response.put("hobbies", userHobbies);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/getUserHobbies/{userId}")
    public ResponseEntity<?> getUserHobbies(@PathVariable Long userId) {
        User user = userService.findById(userId);
        System.out.println(userId);
        if (user != null) {
            String userHobbies = user.getHobby();
            System.out.println(userHobbies);
            Map<String, Object> response = new HashMap<>();
            response.put("hobbies", userHobbies);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/checkUserOrAdminOrGroupOwner")
    @ResponseBody
    public ResponseEntity<List<Object>> checkUserOrAdminOrGroupOwner() {
        return ResponseEntity.ok(userService.checkUserOrAdminOrGroupOwner());
    }
    @PostMapping("/checkPassword")
    @ResponseBody
    public ResponseEntity<String> verifyPassword(@RequestBody Map<String, String> requestBody) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> user = userService.findByStaffId(staffId);
        if (user.isPresent()) {
            String storedPassword = user.get().getPassword();
            String currentPassword = requestBody.get("password");
            System.out.println(storedPassword + " is here");
            System.out.println(currentPassword);
            if (passwordEncoder.matches(currentPassword, storedPassword)) {
                System.out.println("matched");
                return ResponseEntity.ok().build();
            } else {
                System.out.println("doesn't match");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
            }
        } else {
            System.out.println("no user");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect User");
        }
    }
    @PostMapping("/saveNewPassword")
    public ResponseEntity<String> saveNewPassword(@RequestBody Map<String, String> requestBody) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        String newpassword = requestBody.get("password");
        Optional<User> optionalUser = userService.findByStaffId(staffId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String encodedPassword = passwordEncoder.encode(newpassword);
            user.setPassword(encodedPassword);
            userRepository.save(user);
            return ResponseEntity.ok("Password saved successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/getCurrentLoginUser")
    @ResponseBody
    public ResponseEntity<User> getCurrentLoginUser() {
        return ResponseEntity.ok(userService.getLogin());
    }

    @GetMapping("/checkIfUserIsAMemberOrOwnerOrAdminOfAGroup/{id}")
    @ResponseBody
    public ResponseEntity<List<Object>> checkIfUserIsAMemberOrOwnerOrAdminOfAGroup(@PathVariable("id") String id) {
        return ResponseEntity.ok(userService.checkIfUserIsAMemberOrOwnerOrAdminOfAGroup(Long.valueOf(id)));
    }

    @PostMapping("/saveSkillToDb")
    @ResponseBody
    public ResponseEntity<Map<String, String>> saveSkillAndShowItBack(@RequestBody SkillDto skillDto) {
        Map<String, String> res = new HashMap<>();
        var user = userService.findByStaffId(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new CommunityHubException("User name not found Exception!"));
        List<User_Skill> user_skills = user_skillService.findByUserId(user.getId());
        List<Skill> listString = new ArrayList<>();
        for (User_Skill user_skill : user_skills) {
            var skill = skillService.findById(user_skill.getSkill().getId());
            listString.add(skill);
        }
        System.out.println("SDFDSF" + skillDto.getName());
        System.out.println("ERERE" + skillDto.getExperience());
        boolean isExisted = listString.stream()
                .anyMatch(u -> u.getName().equals(skillDto.getName().trim()));
        System.out.println("DFDSF" + isExisted);
        var skillObj = skillService.findByName(skillDto.getName().trim());
        if (skillObj != null && isExisted) {
            var userSkill = user_skillService.findBySkillId(skillObj.getId());
            var user_Skill = User_Skill.builder()
                    .skill(skillObj)
                    .user(user)
                    .experience(skillDto.getExperience())
                    .build();
            user_skillService.save(user_Skill);
            res.put("id", skillObj.getId().toString());
            res.put("name", skillDto.getName());
            res.put("experience", skillDto.getExperience());
            return ResponseEntity.status(HttpStatus.OK).body(res);
        } else {
            var skill = Skill.builder()
                    .name(skillDto.getName())
                    .build();
            var svgSkill = skillService.saveSkill(skill);
            var user_skill = User_Skill.builder()
                    .skill(svgSkill)
                    .user(user)
                    .experience(skillDto.getExperience())
                    .build();
            user_skillService.save(user_skill);
            res.put("id", svgSkill.getId().toString());
            res.put("name", skillDto.getName());
            res.put("experience", skillDto.getExperience());

            return ResponseEntity.status(HttpStatus.OK).body(res);

        }
    }

    @GetMapping("/deleteSkill/{id}")
    public ResponseEntity<Map<String, String>> deleteSkill(@PathVariable("id") Long id) {
        user_skillService.deleteSkillById(id);
        //skillRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Delete Successful");
        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PostMapping("/updateSkill/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateSkill(
            @PathVariable("id") Long id,
            @RequestBody SkillDto skillDto) {

        System.out.println("nnn" + id);
        String staffId = SecurityContextHolder.getContext().getAuthentication().getName().trim();
        Optional<User> optionalUser = userService.findByStaffId(staffId);

        if (optionalUser.isPresent()) {
            try {
                User user = optionalUser.get();
                Optional<User_Skill> optionalUserSkill = userSkillRepository.findBySkillIdAndUserId(id,user.getId());

                if (optionalUserSkill.isPresent()) {
                    User_Skill userSkill = optionalUserSkill.get();
                    Skill skill = userSkill.getSkill();

                    String newName = skillDto.getName();
                    String newExperience = skillDto.getExperience();

                    if (newName != null && !newName.isEmpty()) {
                        skill.setName(newName);
                    }
                    if (newExperience != null && !newExperience.isEmpty()) {
                        userSkill.setExperience(newExperience);
                    }

                    skillRepository.save(skill);
                    userSkillRepository.save(userSkill);

                    Map<String, String> response = new HashMap<>();
                    response.put("id", id.toString());
                    response.put("name", skill.getName());
                    response.put("experience", userSkill.getExperience());
                    response.put("message", "Update Successful");

                    return ResponseEntity.status(HttpStatus.OK).body(response);
                } else {
                    throw new RuntimeException("Skill not found for user.");
                }
            } catch (Exception e) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", "An error occurred while updating the skill: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping("/policyExists")
    public ResponseEntity<Boolean> policyExists() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> optionalUser = userService.findByStaffId(staffId);
        if (optionalUser.isPresent()) {
        boolean exists = policyService.policyExistsForUser(optionalUser.get().getId());
        return ResponseEntity.ok(exists);
        }
        return ResponseEntity.ok(false);
    }
}


