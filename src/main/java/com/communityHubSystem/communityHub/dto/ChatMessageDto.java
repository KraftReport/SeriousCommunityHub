package com.communityHubSystem.communityHub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ChatMessageDto {

    private Long roomId;
    private String sender;
    private String content;
}
