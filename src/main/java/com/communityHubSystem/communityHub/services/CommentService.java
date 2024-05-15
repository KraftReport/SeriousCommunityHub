package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.dto.CommentUpdateDto;
import com.communityHubSystem.communityHub.models.Comment;

import java.util.List;

public interface CommentService {

    public Comment save(Comment comment);

    public Comment findByPostId(Long id);

   public List<Comment> findCommentsByPostId(Long id);

  public Comment findById(Long postId);

  public void deleteComment(Long id);

    void updateComment(CommentUpdateDto commentUpdateDto);

   public Long findCommentSizeByPostId(Long id);
}
