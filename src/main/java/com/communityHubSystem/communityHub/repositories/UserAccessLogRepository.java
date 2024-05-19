package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.UserAccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAccessLogRepository extends JpaRepository<UserAccessLog,Long> {

    List<UserAccessLog> findAllByEmailOrderByAccessTimeDesc(String trim);
    Page<UserAccessLog> findAllByEmailOrderByAccessTimeDesc(String email, Pageable pageable);
}
