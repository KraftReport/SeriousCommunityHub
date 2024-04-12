package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.User_ChatRoom;

import java.util.List;

public interface User_ChatRoomService {

    public List<User_ChatRoom> findByUserId(Long id);
   public void createdRoom(String name,List<Long> selectedUserIds);
}
