package com.communityHubSystem.communityHub.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "policy")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Policy implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 6000)
    private String rule;
    private Date date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
