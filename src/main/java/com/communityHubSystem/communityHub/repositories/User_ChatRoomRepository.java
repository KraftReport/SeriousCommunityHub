package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.User_ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface User_ChatRoomRepository extends JpaRepository<User_ChatRoom,Long> {

    List<User_ChatRoom> findByUserId(Long id);

    List<User_ChatRoom> findByChatRoomId(Long id);

    User_ChatRoom findByUserIdAndChatRoomId(Long id, Long id1);
}
