package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
}
