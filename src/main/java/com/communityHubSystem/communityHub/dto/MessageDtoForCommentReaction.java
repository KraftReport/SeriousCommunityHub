package com.communityHubSystem.communityHub.dto;

import com.communityHubSystem.communityHub.models.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDtoForCommentReaction {

    private Long postId;
    private Long commentId;
    private String staffId;
    private String sender;
    private String content;
    private Type type;
    private String photo;
    private Date date;
}
