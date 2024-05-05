package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.Background;
import com.communityHubSystem.communityHub.models.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface BackgroundService {
    void create(Background background, Long userId);

    public String saveBackground(MultipartFile file, User user) throws IOException;

    List<Background> findByUserId(Long userId);

    //public String getUserBackgroundImage(Long userId);
}
