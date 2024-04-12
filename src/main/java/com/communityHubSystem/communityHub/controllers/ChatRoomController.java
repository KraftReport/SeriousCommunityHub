package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.models.ChatRoom;
import com.communityHubSystem.communityHub.models.User_ChatRoom;
import com.communityHubSystem.communityHub.services.ChatRoomService;
import com.communityHubSystem.communityHub.services.UserService;
import com.communityHubSystem.communityHub.services.User_ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UserService userService;
    private final User_ChatRoomService user_chatRoomService;

    @GetMapping("/room-list")
    public ResponseEntity<List<ChatRoom>> findAllChatRoom() {
        String staffId = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userService.findByStaffId(staffId).orElse(null);
        List<User_ChatRoom> user_chatRooms = user_chatRoomService.findByUserId(user.getId());
        List<ChatRoom> chatRooms = new ArrayList<>();
        for (User_ChatRoom user_chatRoom : user_chatRooms) {
            ChatRoom chatRoom = chatRoomService.findById(user_chatRoom.getChatRoom().getId());
            chatRooms.add(chatRoom);
        }
        return ResponseEntity.status(HttpStatus.OK).body(chatRooms);
    }
}
