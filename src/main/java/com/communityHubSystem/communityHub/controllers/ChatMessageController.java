package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.dto.ChatRoomGroupDto;
import com.communityHubSystem.communityHub.dto.NotificationDtoForChatRoom;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.ChatMessage;
import com.communityHubSystem.communityHub.models.ChatRoom;
import com.communityHubSystem.communityHub.services.ChatMessageService;
import com.communityHubSystem.communityHub.services.UserService;
import com.communityHubSystem.communityHub.services.User_ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final User_ChatRoomService user_chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    @MessageMapping("/chat")
    public void processMessage(@Payload Map<String, Object> payload) {
        Long roomId = Long.parseLong(payload.get("id").toString());
        String staffId = payload.get("sender").toString();
        System.out.println("SOMETHING"+roomId);
        var user = userService.findByStaffId(staffId.trim()).orElseThrow(() -> new CommunityHubException("User Name Not Found Exception"));
        String content = payload.get("content").toString();
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(ChatRoom.builder().id(roomId).build())
                .date(new Date())
                .sender(staffId)
                .content(content)
                .build();
        chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSend( "/user/chatRoom/queue/messages", new NotificationDtoForChatRoom(
                roomId,
                user.getStaffId(),
                content
        ));
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable("id") Long id) {
        return ResponseEntity.ok(chatMessageService.findChatMessagesByRoomId(id));
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
