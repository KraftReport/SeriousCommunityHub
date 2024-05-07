package com.communityHubSystem.communityHub.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "skill")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;


    @OneToMany(mappedBy = "skill",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User_Skill> user_skills;
}
