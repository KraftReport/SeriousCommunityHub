package com.communityHubSystem.communityHub.impls;

import com.cloudinary.Cloudinary;
import com.communityHubSystem.communityHub.models.ChatMessage;
import com.communityHubSystem.communityHub.models.ChatRoom;
import com.communityHubSystem.communityHub.repositories.ChatMessageRepository;
import com.communityHubSystem.communityHub.services.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatMessageImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final List<String> photoExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", "bmp","tiff","tif","psv","svg","webp","ico","heic");
    private final Cloudinary cloudinary;

   @Transactional
    @Override
    public void save(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }

    @Override
    public List<ChatMessage> findChatMessagesByRoomId(Long id) {
        return chatMessageRepository.findChatMessagesByChatRoomId(id);
    }

    @Override
    public ChatMessage saveWithAttachment(Long id, MultipartFile file, String sender, String date) throws IOException {
        if(isValidPhotoExtension(getFileExtension(file))){
           ChatMessage chatMessage = ChatMessage.builder()
                   .chatRoom(ChatRoom.builder().id(id).build())
                   .date(new Date())
                   .sender(sender)
                   .content(uploadPhoto(file))
                   .build();
            chatMessageRepository.save(chatMessage);
           return chatMessage;
        }else {
            return null;
        }
    }


    public String uploadPhoto(MultipartFile file) throws IOException {
        return cloudinary.uploader()
                .upload(file.getBytes(), Map.of( "public_id", UUID.randomUUID().toString()))
                .get("url").toString();
    }

    public boolean isValidPhotoExtension(String extension) {
        return photoExtensions.contains(extension);
    }

    public String getFileExtension(MultipartFile file){
        return file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')).toLowerCase();
    }
}
