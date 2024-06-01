package com.communityHubSystem.communityHub.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Table(name = "invitation")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long senderId;
    private Long recipientId;
    private boolean isInvited;
    private boolean isRemoved;
    private boolean isAccepted;
    private boolean isRequested;
    private Date date;

    @ManyToOne
    @JoinColumn(name = "communityId")
    private Community community;
}
