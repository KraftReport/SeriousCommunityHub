package com.communityHubSystem.communityHub.controllers;

        import com.communityHubSystem.communityHub.models.ChatRoom;
        import com.communityHubSystem.communityHub.models.User;
        import com.communityHubSystem.communityHub.models.User_ChatRoom;
        import com.communityHubSystem.communityHub.models.User_Group;
        import com.communityHubSystem.communityHub.services.*;
        import lombok.RequiredArgsConstructor;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.security.core.context.SecurityContextHolder;
        import org.springframework.stereotype.Controller;
        import org.springframework.web.bind.annotation.*;

        import java.util.ArrayList;
        import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("user")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UserService userService;
    private final User_ChatRoomService user_chatRoomService;
    private final CommunityService communityService;
    private final User_GroupService user_groupService;

    @GetMapping("/room-list")
    @ResponseBody
    public ResponseEntity<List<ChatRoom>> findAllChatRoom() {
        String staffId = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userService.findByStaffId(staffId).orElse(null);
        List<User_ChatRoom> user_chatRooms = user_chatRoomService.findByUserId(user.getId());
        List<ChatRoom> chatRooms = new ArrayList<>();
        for (User_ChatRoom user_chatRoom : user_chatRooms) {
            ChatRoom chatRoom = chatRoomService.findById(user_chatRoom.getChatRoom().getId());
             if(chatRoom.isDeleted()) {
                 chatRooms.add(chatRoom);
             }
             }
        return ResponseEntity.status(HttpStatus.OK).body(chatRooms);
    }

    @GetMapping("/chat-room")
    public String chatPage() {
        return "/user/user-chat";
    }

    @GetMapping("/room-photo/{id}")
    @ResponseBody
    public ResponseEntity<ChatRoom> getChatRoom(@PathVariable("id") Long id) {
        var chatRoom = chatRoomService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(chatRoom);
    }

    @GetMapping("/room-member-size/{id}")
    @ResponseBody
    public ResponseEntity<?> getChatRoomSize(@PathVariable("id") Long id) {
        var user_chat_room = user_chatRoomService.findByChatRoomId(id);
        List<User_ChatRoom> userChatRooms = new ArrayList<>();
        for(User_ChatRoom user_chatRoom:user_chat_room){
            if(!user_chatRoom.getUser().getId().equals(999)){
                userChatRooms.add(user_chatRoom);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(userChatRooms.size());
    }

    @GetMapping("/member-list-chatRoom/{id}")
    @ResponseBody
    public ResponseEntity<List<User>> getMembers(@PathVariable("id") Long id) {
        var chatRoom = chatRoomService.findById(id);
        var userList = new ArrayList<>();
        var user_chatRooms = user_chatRoomService.findByChatRoomId(id);
        for (User_ChatRoom user_chatRoom : user_chatRooms) {
            userList.add(user_chatRoom.getUser().getId());
        }
        List<User> notMemberList = new ArrayList<>();
        List<User_Group> user_groups = user_groupService.findByCommunityId(chatRoom.getCommunity().getId());
        for (User_Group user_group : user_groups) {
            if (!userList.contains(user_group.getUser().getId())) {
                var user = userService.findById(user_group.getUser().getId());
                if(!user.getId().equals(999)){
                    notMemberList.add(user);
                }
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(notMemberList);
    }

    @GetMapping("/chat-room-memberList/{id}")
    @ResponseBody
    public ResponseEntity<List<User>> getAllUsersForChatRoom(@PathVariable("id") Long id) {
        List<User_ChatRoom> user_chatRooms = user_chatRoomService.findByChatRoomId(id);
        List<User> userList = new ArrayList<>();
        for (User_ChatRoom user_chatRoom : user_chatRooms) {
            var user = userService.findById(user_chatRoom.getUser().getId());
            if(!user.getId().equals(999)){
                userList.add(user);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

}
