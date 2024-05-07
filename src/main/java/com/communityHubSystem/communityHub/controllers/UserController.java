package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.dto.UserDTO;
import com.communityHubSystem.communityHub.models.Policy;
import com.communityHubSystem.communityHub.models.Skill;
import com.communityHubSystem.communityHub.models.User;
import com.communityHubSystem.communityHub.models.User_Skill;
import com.communityHubSystem.communityHub.repositories.*;
import com.communityHubSystem.communityHub.services.PostService;
import com.communityHubSystem.communityHub.services.UserService;
import lombok.RequiredArgsConstructor;
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
        System.out.println(user);
        System.out.println(user.getPosts().size());
        model.addAttribute("user", user);
        return "/user/user-profile";
    }

    @PostMapping("/upload-data")
    @ResponseBody
    public ResponseEntity<?> uploadUsersData(@RequestParam("file") MultipartFile file) throws IOException {
        this.userService.saveUserToDatabase(file);
        return ResponseEntity
                .ok(Map.of("Message", " Employee data uploaded and saved to database successfully"));
    }

    @GetMapping("/View-all-users")
    public String viewUser(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "/user/view-all-user";
    }

    @GetMapping("/getSkills")
    @ResponseBody
    public ResponseEntity<List<Skill>> getSkills() {
        var skills = skillRepository.findAll();
        System.out.println(skills);
        return ResponseEntity.ok(skillRepository.findAll());
    }

    @PostMapping("/savePassword")
    public ResponseEntity<String> savePassword(@RequestBody Map<String, String> requestBody) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> optionalUser = userService.findByStaffId(staffId);
        if (optionalUser.isPresent()) {
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
            userRepository.save(user);
        }
        return ResponseEntity.ok("Password saved");
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
            String experience = requestBody.get("experience");

            // Split the selected skills string into an array of skill names
            String[] selectedSkillsArray = selectedSkillsString.split(",");

            // Iterate through the skill names
            for (String skillName : selectedSkillsArray) {
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

                // Create User_Skill association
                User_Skill userSkill = new User_Skill();
                userSkill.setUser(user);
                userSkill.setSkill(skill);
                userSkill.setExperience(experience);
                userSkillRepository.save(userSkill);
            }

            // Save the updated user entity
            userRepository.save(user);
            return ResponseEntity.ok("Skills saved");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @PostMapping("/updateProfilePhoto")
    @ResponseBody
    public ResponseEntity<?> updateProfilePhoto(@ModelAttribute UserDTO userDTO) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(userService.updateProfilePhoto(userDTO.getFile()));
    }

    @PostMapping("/saveImage")
    public ResponseEntity<?> saveImage(@RequestBody byte[] image) throws IOException {
        System.out.println("Image file name: " + Arrays.toString(image));
        var ips = new ByteArrayInputStream(image);
        var photo = new MockMultipartFile(Arrays.toString(image), ips);
        userService.saveImage(photo);
        // Return a success response
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

    @GetMapping("/userSearchMethod/{input}")
    @ResponseBody
    public ResponseEntity<List<User>> userSearchMethod(@PathVariable("input")String input) throws UnsupportedEncodingException {
        return ResponseEntity.ok(userService.userSearchMethod(input));
    }
}


