package com.communityHubSystem.communityHub.dto;

import com.communityHubSystem.communityHub.models.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViewPostDto {

    private Long id;
    private String description;
    private Date createdDate;
    @Enumerated(EnumType.STRING)
    private PostType postType;
    @Enumerated(EnumType.STRING)
    private Access access;
    private User user;
    private List<Resource> resources;
    private User_Group user_group;
    private Set<React> reacts;
    private Set<Comment> comments;
    private Set<Share> shares;
    private Set<Notification> notifications ;

    public enum PostType{
        EVENT,CONTENT,POLL,RESOURCE
    }
}
