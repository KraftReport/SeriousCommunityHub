package com.communityHubSystem.communityHub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollDto {
    private String  eventId;
    private String voteType;
}
