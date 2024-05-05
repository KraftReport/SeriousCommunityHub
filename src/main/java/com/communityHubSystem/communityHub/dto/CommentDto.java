package com.communityHubSystem.communityHub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long postId;
    private Long commentId;
    private Long replyId;
    private String staffId;
    private String sender;
    private String content;
    private String photo;
    private Date date;
}
