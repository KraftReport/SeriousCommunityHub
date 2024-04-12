package com.communityHubSystem.communityHub.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "event")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private Date created_date;
    private Date start_date;
    private Date end_date;
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    private Access access;
    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "event",cascade = CascadeType.ALL)
    private List<Poll> polls;

    @ManyToOne
    @JoinColumn(name = "user_group_id")
    private User_Group user_group;

    public enum EventType{
        EVENT,VOTE
    }

}
