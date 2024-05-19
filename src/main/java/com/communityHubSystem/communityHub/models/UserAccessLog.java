package com.communityHubSystem.communityHub.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_access_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String errorMessage;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime accessTime;
    @Enumerated(EnumType.STRING)
    private LoginType type;

    public UserAccessLog(String email, LoginType type, LocalDateTime accessTime) {
        this.email = email;
        this.type = type;
        this.accessTime = accessTime;
    }

    public UserAccessLog(String email, String errorMessage, LoginType type, LocalDateTime accessTime) {
        this.email = email;
        this.type = type;
        this.accessTime = accessTime;
        this.errorMessage = errorMessage;
    }


    public enum LoginType {
        SIGN_IN, SIGN_OUT, ERROR;
    }
}
