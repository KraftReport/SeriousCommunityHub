package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.dto.NotificationDtoForChatRoom;
import com.communityHubSystem.communityHub.models.ChatMessage;
import com.communityHubSystem.communityHub.models.ChatRoom;
import com.communityHubSystem.communityHub.services.ChatMessageService;
import com.communityHubSystem.communityHub.services.User_ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final User_ChatRoomService user_chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void processMessage(@Payload Map<String, Object> payload) {
        Long roomId = Long.parseLong(payload.get("id").toString());
        String sender = payload.get("sender").toString();
        String content = payload.get("content").toString();
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(ChatRoom.builder().id(roomId).build())
                .date(new Date())
                .sender(sender)
                .content(content)
                .build();
        chatMessageService.save(chatMessage);
        messagingTemplate.convertAndSendToUser(roomId.toString(), "/queue/messages", new NotificationDtoForChatRoom(
                roomId,
                content,
                sender
        ));
    }

    @GetMapping("/message/{id}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(@PathVariable("id") Long id) {
        return ResponseEntity.ok(chatMessageService.findChatMessagesByRoomId(id));
    }

    @PostMapping("/group-add")
    @ResponseBody
    public ResponseEntity<Map<String, String>> createdGroup(@RequestParam("name") String name, @RequestParam("selectedUserIds") List<Long> selectedUserIds) {
        user_chatRoomService.createdRoom(name, selectedUserIds);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Group created successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
