package com.communityHubSystem.communityHub.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EventNotiDto {

    private String userId;
    private String content;
    private Long groupId;
    private Status status;

    public enum Status{
        PUBLIC,PRIVATE
    }
}
