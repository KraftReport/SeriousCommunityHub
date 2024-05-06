package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.React;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReactRepository extends JpaRepository<React,Long> {
     React findByPostIdAndUserId(Long id, Long id1);

    List<React> findByPostId(Long id);

    React findReactByUserIdAndCommentId(Long id, Long commentId);

    React findReactByUserIdAndPostIdAndCommentId(Long userId, Long postId, Long commentId);

    React findReactByUserIdAndPostIdAndCommentIdAndReplyId(Long userId, Long postId, Long commentId, Long replyId);

    React findByCommentId(Long id);

    React findByReplyId(Long id);
}
