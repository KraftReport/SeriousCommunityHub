package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply,Long> {

    List<Reply> findAllByCommentId(Long id);

    @Query("SELECT COUNT(r) FROM Reply r WHERE r.comment.id = :commentId")
    Long countRepliesByPostId(@Param("commentId") Long commentId);
}
