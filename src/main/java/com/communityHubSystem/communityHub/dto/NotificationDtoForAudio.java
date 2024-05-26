package com.communityHubSystem.communityHub.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class NotificationDtoForAudio {

    private Long chatId;
    private Long id;
    private String sender;
    private String voiceUrl;
}
