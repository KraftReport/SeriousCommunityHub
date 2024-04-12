package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.UserAccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccessLogRepository extends JpaRepository<UserAccessLog,Long> {
}
