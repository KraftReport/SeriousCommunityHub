package com.communityHubSystem.communityHub.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "saved_post")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavedPosts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long saverId;
    private Long postId;
    private Date savedDate;
}
