package com.communityHubSystem.communityHub.impls;
import com.communityHubSystem.communityHub.models.SavedPosts;
import com.communityHubSystem.communityHub.repositories.*;
import com.communityHubSystem.communityHub.services.SavedPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class SavedPostServiceImpl implements SavedPostService {
    private final SavedPostRepository savedPostRepository;
    @Override
    public SavedPosts saveAPost(Long userId, Long postId) {
        var post = SavedPosts.builder()
                .postId(postId)
                .savedDate(new Date())
                .saverId(userId)
                .build();
        return savedPostRepository.save(post);
    }

    @Override
    public SavedPosts unSaveAPost(Long userId, Long postId) {
        var post = savedPostRepository.findByUserIdAndPostId(userId,postId);
        savedPostRepository.delete(post);
        return new SavedPosts();
    }
}
