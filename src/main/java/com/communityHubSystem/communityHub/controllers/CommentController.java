package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.dto.CommentUpdateDto;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.Notification;
import com.communityHubSystem.communityHub.models.User;
import com.communityHubSystem.communityHub.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Map<String,Long>> deletedComment(@PathVariable("id")Long id){
        var loginUser = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userService.findByStaffId(loginUser).orElseThrow(() -> new CommunityHubException("user name not found exception"));
        Map<String,Long> response = new HashMap<>();
        var comment = commentService.findById(id);
        var post = postService.findById(comment.getPost().getId());
        response.put("postId",post.getId());
        var noti = notificationService.findByCommentIdAndUserId(id,user.getId());
        for(Notification notification:noti){
            notificationService.deleteAll(notification.getId());
        }
        var react = reactService.findReactByCommentId(id);
        if(react != null){
            reactService.deleteById(react.getId());
        }
        commentService.deleteComment(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete-reply/{id}")
    public ResponseEntity<Map<String,Long>> deletedReply(@PathVariable("id")Long id){
        var loginUser = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userService.findByStaffId(loginUser).orElseThrow(() -> new CommunityHubException("user name not found exception"));
        Map<String,Long> response = new HashMap<>();
        var reply = replyService.findById(id);
        var post = postService.findById(reply.getComment().getPost().getId());
        response.put("postId",post.getId());
        var noti = notificationService.findByReplyIdAndUserId(id,user.getId());
        for(Notification notification:noti){
            notificationService.deleteAll(notification.getId());
        }
        var react = reactService.findByReplyId(id);
        if(react != null){
            reactService.deleteById(react.getId());
        }
        replyService.deleteReply(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/comment-update")
    public ResponseEntity<Map<String,Long>> updateComment(@RequestBody CommentUpdateDto commentUpdateDto){
        Map<String,Long> response = new HashMap<>();
        var comment = commentService.findById(commentUpdateDto.getId());
        var post = postService.findById(comment.getPost().getId());
        response.put("postId",post.getId());
        commentService.updateComment(commentUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/reply-update")
    public ResponseEntity<Map<String,Long>> updateReply(@RequestBody CommentUpdateDto commentUpdateDto){
        Map<String,Long> response = new HashMap<>();
        var reply = replyService.findById(commentUpdateDto.getId());
        var post = postService.findById(reply.getComment().getPost().getId());
        System.out.println("Post getId"+post.getId());
        response.put("postId",post.getId());
        replyService.updatedReply(commentUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/add-user-chat-room")
    public ResponseEntity<Map<String,String>> addUserForChatRoom(@RequestParam("id") Long id,@RequestParam(required = false) List<Long> selectedIds  ) {
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
    public ResponseEntity<Map<String,String>> kickUserForChatRoom(@RequestParam("id") Long id,@RequestParam(required = false) List<Long> selectedIds  ) {
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

    @GetMapping("/get-all-active-user")
    public ResponseEntity<List<User>> getAllActiveUsers(){
        var users = userService.getAllUser();
        List<User> userList = new ArrayList<>();
        for(User user:users){
            if(user.isActive() == true){
                userList.add(user);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }
}
