package com.communityHubSystem.communityHub.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "reply")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Reply implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Date date;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
