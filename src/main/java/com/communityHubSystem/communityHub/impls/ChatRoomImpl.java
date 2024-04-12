package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.models.ChatRoom;
import com.communityHubSystem.communityHub.repositories.ChatRoomRepository;
import com.communityHubSystem.communityHub.services.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ChatRoomImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    @Override
    public ChatRoom save(String name) {
        ChatRoom chatRoom = ChatRoom.builder()
                .date(new Date())
                .name(name)
                .build();
   return chatRoomRepository.save(chatRoom);

    }

    @Override
    public ChatRoom findById(Long id) {
        return chatRoomRepository.findById(id).orElseThrow();
    }
}
