package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.ChatMessage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface ChatMessageService {

  public ChatMessage save(ChatMessage chatMessage);
  public List<ChatMessage> findChatMessagesByRoomId(Long id);

    public ChatMessage saveWithAttachment(Long id, MultipartFile file, String sender, String date) throws IOException;

    public ChatMessage saveWithAudio(MultipartFile file, Long id, String sender, String date) throws IOException;

   public void deleteByDate(Long id,Long date);

    void deleteById(Long id);
}
