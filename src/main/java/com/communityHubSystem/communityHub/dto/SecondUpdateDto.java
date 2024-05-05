package com.communityHubSystem.communityHub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecondUpdateDto {

    private String resourceId;
    private String postCaption;
    private String postUrl;
}
