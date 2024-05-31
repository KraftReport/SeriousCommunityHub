package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.dto.NotificationDto;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.Notification;
import com.communityHubSystem.communityHub.models.User;
import com.communityHubSystem.communityHub.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;
    private final ReactService reactService;
    private final CommentService commentService;
    private final ReplyService replyService;


    @GetMapping("/get-all-noti/{page}")
    public ResponseEntity<List<NotificationDto>> getAllNotiByLoginUser(@PathVariable("page") String page){
        var staffId = SecurityContextHolder.getContext().getAuthentication().getName();
        var loginUser = userService.findByStaffId(staffId).orElseThrow(() -> new CommunityHubException("user name not found exception"));
        Page<NotificationDto> notificationList = notificationService.findAllByUserId(loginUser.getId(), page);
        System.out.println("Notification Page"+page);
        System.out.println("Notification List"+notificationList.getContent());
        return ResponseEntity.status(HttpStatus.OK).body(notificationList.getContent());
    }

    @GetMapping("/like-noti-type/{id}")
    public ResponseEntity<?> getReactType(@PathVariable("id")Long id){
        return ResponseEntity.status(HttpStatus.OK).body(reactService.findById(id));
    }

    @GetMapping("/comment-user-data/{id}")
    public ResponseEntity<?> getCommentedUser(@PathVariable("id")Long id){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.findById(id));
    }

    @GetMapping("/reply-user-data/{id}")
     public ResponseEntity<?> getRepliedUser(@PathVariable("id")Long id){
        return ResponseEntity.status(HttpStatus.OK).body(replyService.findById(id));
    }

    @PutMapping("/turn-off-noti")
    public ResponseEntity<?> turnOffNoti(@RequestBody Map<String, String> requestBody) {
        String isOn = requestBody.get("isOn");
        var loginUser = getUserForIsOn();
         userService.notiChangeToTurnOff(loginUser);
        System.out.println("DDS" + isOn);
        return ResponseEntity.ok("Turn off successfully!");
    }

    @PutMapping("/turn-on-noti")
    public ResponseEntity<?> turnOnNoti(@RequestBody Map<String, String> requestBody) {
        String isOn = requestBody.get("isOn");
        var loginUser = getUserForIsOn();
        userService.notiChangeToTurnOn(loginUser);
        System.out.println("DDS" + isOn);
        return ResponseEntity.ok("Turn on successfully!");
    }


    @GetMapping("/check-notiStatus")
    public ResponseEntity<?> checkNotiStatusForUser(){
        return ResponseEntity.status(HttpStatus.OK).body(getUserForIsOn());
    }

    public User getUserForIsOn(){
        var user = userService.findByStaffId(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new CommunityHubException("User name not found exception!"));
        return user;
    }
}
