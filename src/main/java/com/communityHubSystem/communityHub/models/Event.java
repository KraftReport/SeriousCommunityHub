package com.communityHubSystem.communityHub.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "event")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private Date created_date;
    private Date start_date;
    private Date end_date;
    private String location;
    private String photo;
    private boolean isDeleted;
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    private Access access;
    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

//    @JsonManagedReference
//    @OneToMany(mappedBy = "event",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
 //  private List<Poll> polls;

    @OneToMany(mappedBy ="voteEvent",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<VoteOption> voteOptions;

    @OneToMany(mappedBy = "event",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Notification> notifications ;

    @OneToMany(mappedBy = "event",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<React> reacts;

    @ManyToOne
    @JoinColumn(name = "user_group_id")
    private User_Group user_group;

    public enum EventType{
        EVENT,VOTE
    }

}
