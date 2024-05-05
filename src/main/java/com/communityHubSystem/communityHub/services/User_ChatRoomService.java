package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.dto.ChatRoomGroupDto;
import com.communityHubSystem.communityHub.models.User_ChatRoom;

import java.io.IOException;
import java.util.List;

public interface User_ChatRoomService {

    public List<User_ChatRoom> findByUserId(Long id);
   public void createdRoom(ChatRoomGroupDto chatRoomGroupDto) throws IOException;

   public List<User_ChatRoom> findByChatRoomId(Long id);

   public void save(User_ChatRoom user_chat_room);

    public   void add(Long id, List<Long> selectedIds);

   public void kick(Long id, List<Long> selectedIds);

   public User_ChatRoom findByUserIdAndChatRoomId(Long id, Long id1);

    void deleteById(Long id);
}
