package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {
}
