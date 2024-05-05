package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.dto.ChatRoomGroupDto;
import com.communityHubSystem.communityHub.models.ChatRoom;
import com.communityHubSystem.communityHub.models.User_ChatRoom;
import com.communityHubSystem.communityHub.repositories.User_ChatRoomRepository;
import com.communityHubSystem.communityHub.services.ChatRoomService;
import com.communityHubSystem.communityHub.services.UserService;
import com.communityHubSystem.communityHub.services.User_ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class User_ChatRoomImpl implements User_ChatRoomService {

    private final User_ChatRoomRepository user_chatRoomRepository;
    private final UserService userService;
    private final ChatRoomService chatRoomService;

    @Override
    public List<User_ChatRoom> findByUserId(Long id) {
        return user_chatRoomRepository.findByUserId(id);
    }

    @Transactional
    @Override
    public void createdRoom(ChatRoomGroupDto chatRoomGroupDto) throws IOException {
        var chatRoom = chatRoomService.save(chatRoomGroupDto);
        for (Long id : chatRoomGroupDto.getUserList()) {
            var user = userService.findById(id);
            User_ChatRoom user_chatRoom = User_ChatRoom.builder()
                    .chatRoom(chatRoom)
                    .date(new Date())
                    .user(user)
                    .build();
            user_chatRoomRepository.save(user_chatRoom);
        }
    }

    @Override
    public List<User_ChatRoom> findByChatRoomId(Long id) {
        return user_chatRoomRepository.findByChatRoomId(id);
    }

    @Override
    public void save(User_ChatRoom user_chat_room) {
        user_chatRoomRepository.save(user_chat_room);
    }

    @Transactional
    @Override
    public void add(Long id, List<Long> selectedIds) {
        var chatRoom = chatRoomService.findById(id);
        for (Long userId : selectedIds) {
            var user = userService.findById(userId);
            var user_chatRoom = User_ChatRoom.builder()
                    .date(new Date())
                    .user(user)
                    .chatRoom(chatRoom)
                    .build();
            user_chatRoomRepository.save(user_chatRoom);
        }
    }

    @Transactional
    @Override
    public void kick(Long id, List<Long> selectedIds) {
        List<User_ChatRoom> user_chatRooms = user_chatRoomRepository.findByChatRoomId(id);
        for (User_ChatRoom user_chatRoom : user_chatRooms) {
           if(selectedIds.contains(user_chatRoom.getUser().getId())){
               user_chatRoomRepository.deleteById(user_chatRoom.getId());
           }
        }
    }

    @Override
    public User_ChatRoom findByUserIdAndChatRoomId(Long id, Long id1) {
        return user_chatRoomRepository.findByUserIdAndChatRoomId(id,id1);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        user_chatRoomRepository.deleteById(id);
    }
}
