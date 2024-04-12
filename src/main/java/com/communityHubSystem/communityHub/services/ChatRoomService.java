package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.ChatRoom;

public interface ChatRoomService {

 public ChatRoom save(String name);
 public ChatRoom findById(Long id);
}
