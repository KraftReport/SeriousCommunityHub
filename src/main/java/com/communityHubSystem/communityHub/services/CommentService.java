package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.Comment;

public interface CommentService {

    public Comment save(Comment comment);

    public Comment findByPostId(Long id);
}
