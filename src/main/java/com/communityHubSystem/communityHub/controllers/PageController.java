package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.models.User;
import com.communityHubSystem.communityHub.services.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    @GetMapping("/checkStaffId")
    @ResponseBody
    public Map<String, Boolean> checkStaffId(@RequestParam String staffId) {
        boolean exists = userService.existsByStaffId(staffId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return response;
    }
    @GetMapping("/")
    public String homePage(HttpSession session){
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> user = userService.findByStaffId(staffId);
        if(user.isPresent() && auth.getAuthorities()
                .stream().anyMatch(a-> ((GrantedAuthority) a)
                        .getAuthority().equals(User.Role.ADMIN.name()) ||
                        ((GrantedAuthority) a)
                                .getAuthority()
                                .equals(User.Role.USER.name()))){
            session.setAttribute("user", user.get());

            return "redirect:/index";
        }else{
            return "/layout/login";
        }
    }

    @GetMapping("/index")
    public String indexPage(Model model,HttpSession session) {
        // Retrieve user object from session
        User user = (User) session.getAttribute("user");
        String encodedPassword = passwordEncoder.encode("DAT@123");
        model.addAttribute("user", user);
        if(user == null) {
            return "redirect:/login";
        }
        return "index";
    }
    @PostMapping("/check-password")
    @ResponseBody
    public Map<String, Boolean> checkPassword(@RequestBody Map<String, String> requestBody) {
        String password = requestBody.get("password");
        boolean match = passwordEncoder.matches("DAT@123", password);
        System.out.println(match);
        Map<String, Boolean> response = new HashMap<>();
        response.put("match", match);
        return response;
    }
    @GetMapping("/video")
    public String videoPage() {
        return "/layout/video";
    }

}
