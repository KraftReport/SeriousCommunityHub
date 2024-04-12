package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.models.User_ChatRoom;
import com.communityHubSystem.communityHub.repositories.User_ChatRoomRepository;
import com.communityHubSystem.communityHub.services.ChatRoomService;
import com.communityHubSystem.communityHub.services.UserService;
import com.communityHubSystem.communityHub.services.User_ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void createdRoom(String name, List<Long> selectedUserIds) {
        var chatRoom = chatRoomService.save(name);
        for (Long id : selectedUserIds) {
            var user = userService.findById(id);
            User_ChatRoom user_chatRoom = User_ChatRoom.builder()
                    .chatRoom(chatRoom)
                    .date(new Date())
                    .user(user)
                    .build();
            user_chatRoomRepository.save(user_chatRoom);
        }
    }
}
