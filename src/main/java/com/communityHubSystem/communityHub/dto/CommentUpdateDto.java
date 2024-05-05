package com.communityHubSystem.communityHub.dto;

import com.communityHubSystem.communityHub.models.Reply;
import com.communityHubSystem.communityHub.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateDto {

  private Long id;
  private String content;
}
