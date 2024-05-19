package com.communityHubSystem.communityHub.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "mention")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Mention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long postedUserId;
    private Date date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

}
