package com.communityHubSystem.communityHub.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ChatRoomDtoForImage {

    private Long id;
    private String content;
    private String sender;
    private MultipartFile file;
    private Date date;
}
