package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.models.Background;
import com.communityHubSystem.communityHub.models.User;
import com.communityHubSystem.communityHub.repositories.BackgroundRepository;
import com.communityHubSystem.communityHub.services.BackgroundService;
import com.communityHubSystem.communityHub.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/background")
@RequiredArgsConstructor
public class BackgroundController {
    private final BackgroundService backgroundService;
    private final UserService userService;
    private final BackgroundRepository backgroundRepository;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createGroup(@ModelAttribute Background background) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> user = userService.findByStaffId(staffId.trim());
        background.setUser(user.get());
        backgroundService.create(background, user.get().getId());
        Map<String,String> response = new HashMap<>();
        response.put("message", "Created successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/view")
    public List<Background> view(){
        List<Background> backgrounds=backgroundRepository.findAll();
        return backgrounds;
    }

    @GetMapping("/user/{userId}")
    @ResponseBody
    public List<Background> getUserBackgrounds(@PathVariable Long userId) {
        return backgroundService.findByUserId(userId);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadBackground(@RequestParam("file") MultipartFile file) throws IOException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> user = userService.findByStaffId(staffId.trim());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        String imageUrl = backgroundService.saveBackground(file, user.get());

        return ResponseEntity.ok().body(imageUrl);
    }

    @DeleteMapping("/delete/{backgroundId}")
    public ResponseEntity<?> deletedBackground(@PathVariable("backgroundId") Long backgroundId){
        backgroundRepository.deleteById(backgroundId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
