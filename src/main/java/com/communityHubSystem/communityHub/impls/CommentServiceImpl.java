package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.models.Comment;
import com.communityHubSystem.communityHub.repositories.CommentRepository;
import com.communityHubSystem.communityHub.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment findByPostId(Long id) {
        return commentRepository.findByPostId(id).orElse(null);
    }
}
