package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    Optional<Comment> findByPostId(Long id);

    List<Comment> findAllByPostId(Long id);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    Long countCommentsByPostId(@Param("postId") Long postId);
}
