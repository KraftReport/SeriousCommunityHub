package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {

    List<ChatMessage> findChatMessagesByChatRoomId(Long id);

    void deleteByDate(Date date);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId AND cm.date = :date")
    ChatMessage findChatMessagesByChatRoomIdAndDate(@Param("roomId") Long roomId, @Param("date") Long date);
}
