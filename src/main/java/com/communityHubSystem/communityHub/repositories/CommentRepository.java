package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    Optional<Comment> findByPostId(Long id);

    List<Comment> findAllByPostId(Long id);
}
