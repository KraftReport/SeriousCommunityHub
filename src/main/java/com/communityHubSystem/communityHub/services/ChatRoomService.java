package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.dto.ChatRoomGroupDto;
import com.communityHubSystem.communityHub.models.ChatRoom;

import java.io.IOException;

public interface ChatRoomService {

 public ChatRoom save(ChatRoomGroupDto chatRoomGroupDto) throws IOException;
 public ChatRoom findById(Long id);

  public  ChatRoom findByCommunityId(Long id);
}
