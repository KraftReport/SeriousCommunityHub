package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.dto.UserDTO;
import com.communityHubSystem.communityHub.models.Skill;
import com.communityHubSystem.communityHub.models.User;
import com.communityHubSystem.communityHub.models.User_Skill;
import com.communityHubSystem.communityHub.repositories.SkillRepository;
import com.communityHubSystem.communityHub.repositories.UserRepository;
import com.communityHubSystem.communityHub.repositories.User_ChatRoomRepository;
import com.communityHubSystem.communityHub.repositories.User_SkillRepository;
import com.communityHubSystem.communityHub.services.PostService;
import com.communityHubSystem.communityHub.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @GetMapping("/allUser")
    @ResponseBody
    public ResponseEntity<List<User>> giveAllUsers(){
        return ResponseEntity.ok(userService.getAllUser());
    }

    @PutMapping("/update")
    @ResponseBody
    public ResponseEntity<?> update(@RequestBody User user){
        userService.updateUserData(user);
        return ResponseEntity.status(HttpStatus.OK).body("Ok");
    }

    @PostMapping("/search")
    @ResponseBody
    public ResponseEntity<List<User>> searchMethod(@RequestBody UserDTO userDTO){
        System.err.println(userDTO);
        userDTO.isActive();
        return ResponseEntity.ok(userService.searchMethod(userDTO));
    }

    @GetMapping("/profile")
    public String profilePage(Model model){
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
                .ok(Map.of("Message" , " Employee data uploaded and saved to database successfully"));
    }

    @GetMapping("/View-all-users")
    public String viewUser(Model model){
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "/user/view-all-user";
    }
    @GetMapping("/getSkills")
    @ResponseBody
    public ResponseEntity<List<Skill>> getSkills() {
        var skills=skillRepository.findAll();
        System.out.println(skills);
        return ResponseEntity.ok(skillRepository.findAll());
    }
    @PostMapping("/updateUserStatus")
    @ResponseBody
    public String updateUserStatus(@RequestParam Long userId, @RequestParam boolean isActive) {
        userService.updateUserStatus(userId, isActive);
        return "User status updated successfully";
    }
    @PostMapping("/savePassword")
    public ResponseEntity<String> savePassword(@RequestBody Map<String, String> requestBody) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> optionalUser = userService.findByStaffId(staffId);
        if (optionalUser.isPresent()) {
            User user =optionalUser.get();
            String password = requestBody.get("password");
            String phoneNumber = requestBody.get("phoneNumber");
            String dob = requestBody.get("dob");
            String gender = requestBody.get("gender");
            String hobbies = requestBody.get("hobbies");
            String encodedPassword=passwordEncoder.encode(password);
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
    @PostMapping("/saveImage")
    public ResponseEntity<?> saveImage(@RequestBody byte[] image) {
        System.out.println("Image file name: " + Arrays.toString(image));

        // Return a success response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Image saved successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}


