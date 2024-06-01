package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.SavedPosts;
import org.springframework.stereotype.Service;

@Service
public interface SavedPostService {
    public SavedPosts saveAPost(Long userId,Long postId);
    public SavedPosts unSaveAPost(Long userId,Long postId);
}
