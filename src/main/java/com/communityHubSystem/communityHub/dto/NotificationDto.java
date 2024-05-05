package com.communityHubSystem.communityHub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private Long id;
    private String content;
    private Date date;
    private Long userId;
    private Long reactId;
    private Long commentId;
    private Long shareId;
    private Long replyId;
    private Long postId;
}
