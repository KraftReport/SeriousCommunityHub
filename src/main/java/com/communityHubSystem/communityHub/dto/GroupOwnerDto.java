package com.communityHubSystem.communityHub.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class GroupOwnerDto {

    private Long communityId;
    private Long userId;
}
