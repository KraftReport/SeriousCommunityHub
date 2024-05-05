package com.communityHubSystem.communityHub.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "poll")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Poll implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    @Enumerated(EnumType.STRING)
    private Vote type;

    @ManyToOne
    @JoinColumn(name = "user_id",unique = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    public enum  Vote {
        YES,NO;
    }

    @ManyToOne
    @JoinColumn(name = "vote_option_id")
    private VoteOption voteOptionId;


}
