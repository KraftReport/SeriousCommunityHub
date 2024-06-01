package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.SavedPosts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedPostRepository extends JpaRepository<SavedPosts, Long>, JpaSpecificationExecutor<SavedPosts> {
    @Query(value = "select * from saved_post where saver_id = :userId and post_id = :postId",nativeQuery = true)
    public SavedPosts findByUserIdAndPostId(Long userId, Long postId);

    @Query(value = "select * from saved_post where saver_id = :id",nativeQuery = true)
    public List<SavedPosts> findByUserId(Long id);
}
