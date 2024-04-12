package com.communityHubSystem.communityHub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
    private String start_date;
    private String  end_date;
    private String description;
    private String eventType;
    private MultipartFile multipartFile;
    private String  groupId;
}
