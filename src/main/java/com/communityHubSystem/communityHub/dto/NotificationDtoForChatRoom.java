package com.communityHubSystem.communityHub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDtoForChatRoom {

    private Long id;
    private String sender;
    private String content;

}
