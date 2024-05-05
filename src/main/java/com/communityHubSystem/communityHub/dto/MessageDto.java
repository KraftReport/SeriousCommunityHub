package com.communityHubSystem.communityHub.dto;

import com.communityHubSystem.communityHub.models.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {

    private Long postId;
    private String staffId;
    private String sender;
    private String content;
    private Type type;
    private String photo;
    private Date date;


    public MessageDto(Long postId, String name, String content, String photo, Date date) {
    }
}
