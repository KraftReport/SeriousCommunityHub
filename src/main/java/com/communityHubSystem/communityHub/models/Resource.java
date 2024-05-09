package com.communityHubSystem.communityHub.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "resource")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Resource implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private String photo;
    private String video;
    private Date date;


    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

}
