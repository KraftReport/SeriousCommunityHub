package com.communityHubSystem.communityHub.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "post")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 6000)
    private String description;
    @Column(nullable = false)
    private Date createdDate;
    @Enumerated(EnumType.STRING)
    private PostType postType;
    @Enumerated(EnumType.STRING)
    private Access access;
    private boolean isDeleted;
    @Column(unique = true)
    private String url;

    public enum PostType{
        EVENT,CONTENT,POLL,RESOURCE
    }


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "post",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Resource> resources;

    @ManyToOne
    @JoinColumn(name = "user_group_id")
    private User_Group userGroup;


    @OneToMany(mappedBy = "post",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<React> reacts;


    @OneToMany(mappedBy = "post",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Comment> comments;


    @OneToMany(mappedBy = "post",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Share> shares;

    @OneToMany(mappedBy = "post",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Notification> notifications ;

    @OneToMany(mappedBy = "post",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Mention> mentions;
}
