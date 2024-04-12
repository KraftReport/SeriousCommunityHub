package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {

    List<ChatMessage> findChatMessagesByChatRoomId(Long id);
}
