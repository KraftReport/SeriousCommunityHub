package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.React;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReactRepository extends JpaRepository<React,Long> {
     React findByPostIdAndUserId(Long id, Long id1);

    List<React> findByPostId(Long id);

    React findReactByUserIdAndCommentId(Long id, Long commentId);

    React findReactByUserIdAndPostIdAndCommentId(Long userId, Long postId, Long commentId);

    React findReactByUserIdAndPostIdAndCommentIdAndReplyId(Long userId, Long postId, Long commentId, Long replyId);

    React findByCommentId(Long id);

    React findByReplyId(Long id);

    List<React> findByEventId(Long id);

    React findByUserIdAndEventId(Long id, Long id1);

    List<React> findByUserId(Long id);

    List<React> findReactByCommentIdAndPostIdAndReplyId(Long commentId, Long id, Long replyId);

    @Query("SELECT COUNT(r) FROM react r WHERE r.comment IS NULL AND r.post.id = :postId AND r.reply IS NULL")
    Long countReactsByPostIdWhereCommentIdIsNullAndReplyIdIsNull(@Param("postId") Long postId);

}
