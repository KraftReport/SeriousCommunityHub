package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply,Long> {

    List<Reply> findAllByCommentId(Long id);
}
