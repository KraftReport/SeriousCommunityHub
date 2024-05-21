package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.User;
import com.communityHubSystem.communityHub.models.UserAccessLog;
import com.communityHubSystem.communityHub.services.UserAccessLogService;
import com.communityHubSystem.communityHub.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserAccessLogController {

    private final UserService userService;
    private final UserAccessLogService userAccessLogService;


    @GetMapping("/get-accessLogForLoginUser/{page}")
    public ResponseEntity<List<UserAccessLog>> getUserAccessLogForLoginUser(@PathVariable("page") String page) {
        var loginUser = getLoginUserForAccessLog();
        Pageable pageable = PageRequest.of(Math.toIntExact(Long.parseLong(page)), 7);

        Page<UserAccessLog> accessLogPage = userAccessLogService.findLogsByEmail(loginUser.getEmail(), pageable);

        return ResponseEntity.ok(accessLogPage.getContent());
    }

    @DeleteMapping("/delete-all-accessLog")
    public ResponseEntity<?> deleteAllAccessLog(){
        var loginUser = getLoginUserForAccessLog();
        userAccessLogService.deleteAllByEmail(loginUser.getEmail().trim());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public User getLoginUserForAccessLog(){
        var staffId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByStaffId(staffId).orElseThrow(() -> new CommunityHubException("User Name Not Found Exception!"));
    }

    public List<UserAccessLog> getUserAccessLog(String email){
       return userAccessLogService.findAllByEmail(email);
    }
}
