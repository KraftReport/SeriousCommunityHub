package com.communityHubSystem.communityHub.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "community")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Community implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private boolean isActive;
    private Date date;
    private String ownerName;
    private String description;
    @Lob
    @Column(name="image",columnDefinition = "LONGBLOB")
    private byte[] image;
    @Transient
    private MultipartFile file;

    @Transient
    private ArrayList<Long> user;

    @OneToMany(mappedBy = "community",cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<User_Group> user_groups;

    @OneToMany(mappedBy = "community",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ChatRoom> chatRooms;
}
