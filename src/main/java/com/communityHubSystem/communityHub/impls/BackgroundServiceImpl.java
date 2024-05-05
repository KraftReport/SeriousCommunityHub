package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.models.Background;
import com.communityHubSystem.communityHub.models.User;
import com.communityHubSystem.communityHub.repositories.BackgroundRepository;
import com.communityHubSystem.communityHub.repositories.UserRepository;
import com.communityHubSystem.communityHub.services.BackgroundService;
import com.communityHubSystem.communityHub.services.ImageUploadService;
import com.communityHubSystem.communityHub.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BackgroundServiceImpl implements BackgroundService {
    private final BackgroundRepository backgroundRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ImageUploadService imageUploadService;

    @Override
    public void create(Background background, Long userId) {
//        MultipartFile file = background.getFile();
//
//        if (file != null) {
//            try {
//                byte[] photoByte = file.getBytes();
//                background.setBackground(photoByte);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//     backgroundRepository.save(background);

    }

    @Override
    public String saveBackground(MultipartFile file, User user) throws IOException {
        String imageUrl = imageUploadService.uploadImage(file);

        Background background = new Background();
        background.setUser(user);
        background.setBackgroundUrl(imageUrl);

        backgroundRepository.save(background);

        return imageUrl;
    }

    @Override
    public List<Background> findByUserId(Long userId) {
        return backgroundRepository.findByUserId(userId);
    }

}

