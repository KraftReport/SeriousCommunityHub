package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.models.User;
import com.communityHubSystem.communityHub.repositories.UserRepository;
import com.communityHubSystem.communityHub.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender emailSender;
    private final UserRepository userRepository;

    @GetMapping("/forgotPassword")
    public String showForgotPasswordPage() {
        return "layout/forgotPassword";
    }

    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendEmail(@RequestBody Map<String, String> requestBody, HttpSession session) {
        System.out.println("Reach here");
        String email = requestBody.get("email");
        if (email != null && !email.isEmpty()) {
            Random rand = new Random();
            int otpValue = rand.nextInt(999999);
            try {
                sendOtpEmail(email, otpValue);
                session.setAttribute("email", email);
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set("otp", String.valueOf(otpValue));
                return ResponseEntity.ok()
                        .headers(responseHeaders)
                        .body("OTP sent successfully");
            } catch (MailSendException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email. Please try again later.");
            } catch (MessagingException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred. Please try again later.");
            }
        } else {
            return ResponseEntity.badRequest().body("Email is required.");
        }
    }

    @PostMapping("/saveNewPassword")
    public ResponseEntity<String> saveNewPassword(@RequestBody Map<String, String> requestBody) {
        String staffId = requestBody.get("staffId");
        String password = requestBody.get("password");

        Optional<User> optionalUser = userService.findByStaffId(staffId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String encodedPassword = passwordEncoder.encode(password);
            user.setPassword(encodedPassword);
            userRepository.save(user);
            return ResponseEntity.ok("Password saved successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/checkStaffId")
    @ResponseBody
    public Map<String, Object> checkStaffId(@RequestParam String staffId) {
        Optional<User> optionalUser = userService.findByStaffId(staffId);
        boolean exists = optionalUser.isPresent();
        boolean donIsNull = !optionalUser.map(User::getDob).isPresent();

        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        response.put("dobIsNull", donIsNull);
        if (optionalUser.isPresent()) {
            boolean accountExpired = optionalUser.get().isActive();
            String bannedReason = optionalUser.get().getBannedReason();
            System.out.println(accountExpired);
            response.put("active", accountExpired);
            response.put("bannedReason", bannedReason);
        }
        return response;
    }

    @GetMapping("/")
    public String homePage(HttpSession session) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> user = userService.findByStaffId(staffId);
        if (user.isPresent() && auth.getAuthorities()
                .stream().anyMatch(a -> ((GrantedAuthority) a)
                        .getAuthority().equals(User.Role.ADMIN.name()) ||
                        ((GrantedAuthority) a)
                                .getAuthority()
                                .equals(User.Role.USER.name()) || a.getAuthority().equals(User.Role.DEFAULT_USER.name()))) {
            session.setAttribute("user", user.get());

            return "redirect:/index";
        } else {
            return "/layout/login";
        }
    }

    @GetMapping("/index")
    public String indexPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        String encodedPassword = passwordEncoder.encode("DAT@123");
        model.addAttribute("user", user);
        if (user == null) {
            return "redirect:/login";
        }
        return "index";
    }

    @PostMapping("/check-password")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkPassword(@RequestBody Map<String, String> requestBody) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> user = userRepository.findByStaffId(staffId);
        if (user.isPresent()) {
            String dbPassword = user.get().getPassword();
            boolean passwordMatch = passwordEncoder.matches("DAT@123", dbPassword);
            String password = requestBody.get("password");
            boolean match = passwordEncoder.matches("DAT@123", password);
            System.out.println(match);
            System.out.println(passwordMatch);
            if (passwordMatch && match) {
                Map<String, Boolean> response = new HashMap<>();
                response.put("match", true);
                return ResponseEntity.ok().body(response);
            } else {
                System.out.println("Passwords don't match");
                return ResponseEntity.ok().body(Collections.singletonMap("match", false));
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/video")
    public String videoPage() {
        return "/layout/video";
    }

    private void sendOtpEmail(String email, int otp) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("Password Reset OTP");
        helper.setText("Your OTP is: " + otp);
        emailSender.send(message);
    }
}
