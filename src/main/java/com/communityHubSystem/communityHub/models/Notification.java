package com.communityHubSystem.communityHub.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "react_id")
    private React react;

    @OneToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @OneToOne
    @JoinColumn(name = "share_id")
    private Share share;

    @OneToOne
    @JoinColumn(name = "reply_id")
    private Reply reply;


}
