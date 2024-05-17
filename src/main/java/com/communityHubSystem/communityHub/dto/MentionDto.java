package com.communityHubSystem.communityHub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MentionDto {

    private Long postId;
    private String userId;
    private List<String> users;
}
