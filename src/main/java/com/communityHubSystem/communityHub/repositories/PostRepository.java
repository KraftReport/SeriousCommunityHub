package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long>, JpaSpecificationExecutor<Post> {
    @Query("SELECT p FROM Post p JOIN FETCH p.resources ORDER BY p.createdDate DESC")
    List<Post> findAllWithResources();

    List<Post> findPostsByUserId(Long id);
}
