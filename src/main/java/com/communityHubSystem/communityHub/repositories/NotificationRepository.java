package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long>, JpaSpecificationExecutor<Notification> {

    Page<Notification> findAllByUserId(Long id, Pageable pageable);

    List<Notification> findByReplyIdAndUserId(Long id, Long id1);

    List<Notification> findByCommentIdAndUserId(Long id, Long id1);

    List<Notification> findByUserId(Long id);

    List<Notification> findAllByCommentId(Long id);

    Notification findByCommentIdAndReplyId(Long id, Long id1);
}
