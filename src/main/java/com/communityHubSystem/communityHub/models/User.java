package com.communityHubSystem.communityHub.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable {

    @Id
    private Long id;
    private String name;
    private String email;
    private String password;
    private String photo;
    private String  phone;
    private String division;
    private String dob;
    private String staffId;
    private Long doorLogNum;
    private String dept;
    private String team;
    private String hobby;
    private String gender;
    private boolean isActive;
    private boolean pending;
    private boolean done;
    private boolean isRemoved;
    private boolean isRejected;
    private boolean isDeleted;
    private String RemovedReason;
    private String RejectReason;
    private String BannedReason;
    private int rejectedCount;
    private boolean fully_permitted;
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Poll> polls;

    @OneToMany(mappedBy = "user",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User_ChatRoom> user_chatRooms;

    @OneToMany(mappedBy = "user",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User_Skill> user_skills;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User_Group> user_groups;

    @OneToMany(mappedBy = "user",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Policy> policies;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Post> posts;

    @OneToMany(mappedBy = "user",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Event> events;

    @OneToMany(mappedBy = "user",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<React> reacts;

    @OneToMany(mappedBy = "user",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Reply> replies;

    @OneToMany(mappedBy = "user",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Comment> comments;

    @OneToMany(mappedBy = "user",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Share> shares;

    @OneToMany(mappedBy = "user",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Notification> notifications;

    @OneToMany(mappedBy = "user",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Mention> mentions;


    public Set<Post> getPosts() {
        if (posts == null) {
            posts = new HashSet<>();
        }
        return posts;
    }


    public enum Role{
        ADMIN,USER,DEFAULT_USER;
    }
}
