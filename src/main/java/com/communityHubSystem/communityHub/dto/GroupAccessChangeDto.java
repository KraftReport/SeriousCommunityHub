package com.communityHubSystem.communityHub.dto;

import com.communityHubSystem.communityHub.models.Community;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class GroupAccessChangeDto {

    private Long communityId;
    @Enumerated(EnumType.STRING)
    private Community.GroupAccess groupAccess;

}
