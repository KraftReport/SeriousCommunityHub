package com.communityHubSystem.communityHub.impls;

import com.cloudinary.Cloudinary;
import com.communityHubSystem.communityHub.dto.ChatRoomGroupDto;
import com.communityHubSystem.communityHub.models.ChatRoom;
import com.communityHubSystem.communityHub.repositories.ChatRoomRepository;
import com.communityHubSystem.communityHub.services.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatRoomImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final List<String> photoExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", "bmp","tiff","tif","psv","svg","webp","ico","heic");
    private final Cloudinary cloudinary;

    @Transactional
    @Override
    public ChatRoom save(ChatRoomGroupDto chatRoomGroupDto) throws IOException {
        ChatRoom chatRoom = new ChatRoom();
        System.out.println("dfsdfsdf"+chatRoomGroupDto.getGroupName());
        System.out.println("dfsdfsdf"+chatRoomGroupDto.getFile());
        chatRoom.setDate(new Date());
        chatRoom.setName(chatRoomGroupDto.getGroupName());
        MultipartFile file = chatRoomGroupDto.getFile();
        if(!file.isEmpty()){
            if(isValidPhotoExtension(getFileExtension(file))){
                chatRoom.setPhoto(uploadPhoto(file));
            }
        }
   return chatRoomRepository.save(chatRoom);

    }

    @Override
    public ChatRoom findById(Long id) {
        return chatRoomRepository.findById(id).orElseThrow();
    }

    @Override
    public ChatRoom findByCommunityId(Long id) {
        return chatRoomRepository.findByCommunityId(id);
    }

    @Transactional
    @Override
    public void deleteChatRoomByCommunityId(Long communityId) {
      var chatRoom = chatRoomRepository.findByCommunityId(communityId);
       if(chatRoom != null){
       chatRoomRepository.findById(chatRoom.getId()).ifPresent(c -> {
           c.setDeleted(false);
           chatRoomRepository.save(c);
       });
       }
    }

    @Transactional
    @Override
    public void saveChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
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
