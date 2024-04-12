package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.models.ChatMessage;
import com.communityHubSystem.communityHub.repositories.ChatMessageRepository;
import com.communityHubSystem.communityHub.services.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

   @Transactional
    @Override
    public void save(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }

    @Override
    public List<ChatMessage> findChatMessagesByRoomId(Long id) {
        return chatMessageRepository.findChatMessagesByChatRoomId(id);
    }
}
