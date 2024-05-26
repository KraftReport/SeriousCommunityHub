package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.dto.CommentUpdateDto;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.Notification;
import com.communityHubSystem.communityHub.models.React;
import com.communityHubSystem.communityHub.models.Type;
import com.communityHubSystem.communityHub.models.User;
import com.communityHubSystem.communityHub.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;
    private final ReplyService replyService;
    private final PostService postService;
    private final User_ChatRoomService user_chatRoomService;
    private final NotificationService notificationService;
    private final UserService userService;
    private final ReactService reactService;


    @DeleteMapping("/delete-comment/{id}")
    public ResponseEntity<Map<String, Long>> deletedComment(@PathVariable("id") Long id) {
        var loginUser = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userService.findByStaffId(loginUser).orElseThrow(() -> new CommunityHubException("user name not found exception"));
        Map<String, Long> response = new HashMap<>();
        var comment = commentService.findById(id);
        var post = postService.findById(comment.getPost().getId());
        response.put("postId", post.getId());
        var noti = notificationService.findByCommentIdAndUserId(id, user.getId());
        for (Notification notification : noti) {
            notificationService.deleteAll(notification.getId());
        }
        var react = reactService.findReactByCommentId(id);
        if (react != null) {
            reactService.deleteById(react.getId());
        }
        commentService.deleteComment(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete-reply/{id}")
    public ResponseEntity<Map<String, Long>> deletedReply(@PathVariable("id") Long id) {
        var loginUser = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userService.findByStaffId(loginUser).orElseThrow(() -> new CommunityHubException("user name not found exception"));
        Map<String, Long> response = new HashMap<>();
        var reply = replyService.findById(id);
        var post = postService.findById(reply.getComment().getPost().getId());
        response.put("postId", post.getId());
        var noti = notificationService.findByReplyIdAndUserId(id, user.getId());
        for (Notification notification : noti) {
            notificationService.deleteAll(notification.getId());
        }
        var react = reactService.findByReplyId(id);
        if (react != null) {
            reactService.deleteById(react.getId());
        }
        replyService.deleteReply(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/comment-update")
    public ResponseEntity<Map<String, Long>> updateComment(@RequestBody CommentUpdateDto commentUpdateDto) {
        Map<String, Long> response = new HashMap<>();
        var comment = commentService.findById(commentUpdateDto.getId());
        var post = postService.findById(comment.getPost().getId());
        response.put("postId", post.getId());
        commentService.updateComment(commentUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/reply-update")
    public ResponseEntity<Map<String, Long>> updateReply(@RequestBody CommentUpdateDto commentUpdateDto) {
        Map<String, Long> response = new HashMap<>();
        var reply = replyService.findById(commentUpdateDto.getId());
        var post = postService.findById(reply.getComment().getPost().getId());
        System.out.println("Post getId" + post.getId());
        response.put("postId", post.getId());
        replyService.updatedReply(commentUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/add-user-chat-room")
    public ResponseEntity<Map<String, String>> addUserForChatRoom(@RequestParam("id") Long id, @RequestParam(required = false) List<Long> selectedIds) {
        Map<String, String> response = new HashMap<>();
        if (selectedIds == null || selectedIds.isEmpty()) {
            response.put("message", "Please select the person you want to add!!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } else {
            user_chatRoomService.add(id, selectedIds);
            response.put("message", "Add Successful");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }


    @PostMapping("/kick-user-chat-room")
    public ResponseEntity<Map<String, String>> kickUserForChatRoom(@RequestParam("id") Long id, @RequestParam(required = false) List<Long> selectedIds) {
        Map<String, String> response = new HashMap<>();
        if (selectedIds == null || selectedIds.isEmpty()) {
            response.put("message", "Please select the person you want to kick!!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } else {
            user_chatRoomService.kick(id, selectedIds);
            response.put("message", "Kick Successful");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    @GetMapping("/get-activeUser-forMention")
    public ResponseEntity<List<User>> getAllUsersForMentionWithoutLoginUser() {
        var users = userService.getAllUser();
        var loginUser = userService.findByStaffId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CommunityHubException("User name not found exception"));

        List<User> userList = users.stream()
                .filter(User::isActive)
                .filter(user -> !user.getId().equals(loginUser.getId()))
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }


    @GetMapping("/get-all-active-user")
    public ResponseEntity<List<User>> getAllActiveUsers() {
        var users = userService.getAllUser();
        List<User> userList = new ArrayList<>();
        for (User user : users) {
            if (user.isActive() == true) {
                userList.add(user);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }


    @GetMapping("/event-react-type/{id}")
    public ResponseEntity<?> getEventReactType(@PathVariable("id") Long id) {
        List<React> reactList = reactService.findByEventId(id);
        List<React> reacts = new ArrayList<>();
        for (React react : reactList) {
            if (!react.getType().equals(Type.OTHER)) {
                reacts.add(react);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(reacts);
    }

    @GetMapping("/event-user-react-type/{id}")
    public ResponseEntity<?> getReactTypeForEventUser(@PathVariable("id") Long id) {
        var staffId = SecurityContextHolder.getContext().getAuthentication().getName();
        var loginUser = userService.findByStaffId(staffId).orElseThrow(() -> new CommunityHubException("User name not found Exception"));
        var react = reactService.findByUserIdAndEventId(loginUser.getId(), id);
        if (react == null) {
            return ResponseEntity.ok(Type.OTHER);
        } else {
            return ResponseEntity.ok(react.getType());
        }
    }

    @DeleteMapping("/delete-noti/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable("id") Long id) {
        notificationService.deleteAll(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete-all-noti")
    public ResponseEntity<?> deleteAllNotForLoginUser() {
        var staffId = SecurityContextHolder.getContext().getAuthentication().getName();
        var loginUser = userService.findByStaffId(staffId).orElseThrow(() -> new CommunityHubException("User Name No found Exception"));
//        List<React> reactList = reactService.findByUserId(loginUser.getId());
        List<Notification> notificationList = notificationService.findByUserId(loginUser.getId());
        if (!notificationList.isEmpty()) {
            notificationService.deleteAllForLoginUser(notificationList);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/posted-user/{id}")
    public ResponseEntity<User> getPostedUser(@PathVariable("id")Long id){
        var post = postService.findById(id);
        User user = null;
        if(post != null){
             user = userService.findById(post.getUser().getId());
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}
