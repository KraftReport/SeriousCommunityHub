package com.communityHubSystem.communityHub.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "chat_room")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoom implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Date date;

    @OneToMany(mappedBy = "chatRoom",cascade = {CascadeType.MERGE,CascadeType.PERSIST},fetch = FetchType.LAZY)
    private Set<User_ChatRoom> user_chatRooms;

    @OneToMany(mappedBy = "chatRoom",cascade = {CascadeType.PERSIST,CascadeType.MERGE},fetch = FetchType.LAZY)
    private Set<ChatMessage> chatMessages;

}
