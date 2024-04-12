package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.ChatMessage;

import java.util.List;

public interface ChatMessageService {

  public void save(ChatMessage chatMessage);
  public List<ChatMessage> findChatMessagesByRoomId(Long id);
}
