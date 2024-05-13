package com.communityHubSystem.communityHub.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitationDto {

    private Long communityId;
    private List<Long> userIds;
}
