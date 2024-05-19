package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.dto.ChatRoomGroupDto;
import com.communityHubSystem.communityHub.dto.MentionDto;
import com.communityHubSystem.communityHub.dto.NotificationDtoForChatRoom;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.*;
import com.communityHubSystem.communityHub.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final User_ChatRoomService user_chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final PostService postService;
    private final MentionService mentionService;
    private final NotificationService notificationService;
    private final CommentService commentService;

    @MessageMapping("/chat")
    public void processMessage(@Payload Map<String, Object> payload) {
        Long roomId = Long.parseLong(payload.get("id").toString());
        String staffId = payload.get("sender").toString();
        System.out.println("SOMETHING" + roomId);
        var user = userService.findByStaffId(staffId.trim()).orElseThrow(() -> new CommunityHubException("User Name Not Found Exception"));
        String content = payload.get("content").toString();
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(ChatRoom.builder().id(roomId).build())
                .date(new Date())
                .sender(staffId)
                .content(content)
                .build();
        chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSend("/user/chatRoom/queue/messages", new NotificationDtoForChatRoom(
                roomId,
                user.getStaffId(),
                content
        ));
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable("id") Long id) {
        return ResponseEntity.ok(chatMessageService.findChatMessagesByRoomId(id));
    }

    @MessageMapping("/mention-notification")
    public void processMentionUser(@Payload MentionDto mentionDto) {
        var loginUser = userService.findByStaffId(mentionDto.getUserId()).orElseThrow(() -> new CommunityHubException("User Name Not Found Exception!"));
        var post = postService.findById(mentionDto.getPostId());
        List<String> stringList = new ArrayList<>();
        String content = "mentioned you in a post";
        for (String staffId : mentionDto.getUsers()) {
            User mentionedUser = userService.findByStaffId(staffId).orElseThrow(() -> new CommunityHubException("User Name Not Found Exception!"));
            stringList.add(mentionedUser.getStaffId());
            var mention = Mention.builder()
                    .postedUserId(loginUser.getId())
                    .date(new Date())
                    .user(mentionedUser)
                    .post(post)
                    .build();
            mentionService.save(mention);
            var noti = Notification.builder()
                    .content(content)
                    .date(new Date())
                    .user(mentionedUser)
                    .mention(mention)
                    .post(post)
                    .build();
            notificationService.save(noti);
        }
        messagingTemplate.convertAndSend("/user/mention/queue/messages", new MentionDto(
                mentionDto.getPostId(),
                mentionDto.getUserId(),
                stringList,
                content
        ));
    }

    @MessageMapping("/mention-notification-forComment")
    public void mentionForComment(@Payload MentionDto mentionDto) {
        var loginUser = userService.findByStaffId(mentionDto.getUserId()).orElseThrow(() -> new CommunityHubException("User Name Not Found Exception!"));
        var comment = commentService.findById(mentionDto.getPostId());
        var post = postService.findById(comment.getPost().getId());
        List<String> listString = new ArrayList<>();
        String content = "mentioned you in a comment";
        for (String staffId : mentionDto.getUsers()) {
            var mentionedUser = userService.findByStaffId(staffId).orElseThrow(() -> new CommunityHubException("User Name Not Found Exception!"));
            listString.add(mentionedUser.getStaffId());
            var mention = Mention.builder()
                    .postedUserId(loginUser.getId())
                    .date(new Date())
                    .user(mentionedUser)
                    .post(post)
                    .comment(comment)
                    .build();

            mentionService.save(mention);
            var noti = Notification.builder()
                    .content(content)
                    .date(new Date())
                    .user(mentionedUser)
                    .mention(mention)
                    .post(post)
                    .build();
            notificationService.save(noti);
        }
        messagingTemplate.convertAndSend("/user/mention/queue/messages", new MentionDto(
                mentionDto.getPostId(),
                mentionDto.getUserId(),
                listString,
                content
        ));
    }

//    @PostMapping("/group-add")
//    @ResponseBody
//    public ResponseEntity<Map<String, String>> createdGroup(@RequestParam("name") String name, @RequestParam("selectedUserIds") List<Long> selectedUserIds) {
//        user_chatRoomService.createdRoom(name, selectedUserIds);
//        Map<String, String> response = new HashMap<>();
//        response.put("message", "Group created successfully");
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

    @PostMapping("/group-add")
    @ResponseBody
    public ResponseEntity<Map<String, String>> createdGroup(@ModelAttribute ChatRoomGroupDto chatRoomGroupDto) throws IOException {
        user_chatRoomService.createdRoom(chatRoomGroupDto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Group created successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
