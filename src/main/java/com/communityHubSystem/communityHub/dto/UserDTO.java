package com.communityHubSystem.communityHub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String staffId;
    private String name;
    private String email;
    private String team;
    private String division;
    private String department;
    private boolean isActive;
    private List<String> skillNameList;
    private MultipartFile file;
}
