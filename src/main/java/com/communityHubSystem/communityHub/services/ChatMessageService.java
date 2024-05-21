package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.ChatMessage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ChatMessageService {

  public void save(ChatMessage chatMessage);
  public List<ChatMessage> findChatMessagesByRoomId(Long id);

    public ChatMessage saveWithAttachment(Long id, MultipartFile file, String sender, String date) throws IOException;
}
