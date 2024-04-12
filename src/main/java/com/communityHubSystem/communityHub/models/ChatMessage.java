package com.communityHubSystem.communityHub.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "chat_message")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private String sender;
    private Date date;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;
}
