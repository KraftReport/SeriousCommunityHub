package com.communityHubSystem.communityHub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomGroupDto {

    private String groupName;
    private List<Long> userList;
    private Long communityId;
    private byte[] photo;
    private MultipartFile file;
}
