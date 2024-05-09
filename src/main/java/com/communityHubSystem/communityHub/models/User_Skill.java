package com.communityHubSystem.communityHub.models;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "user_skill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User_Skill implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String experience;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;
}
