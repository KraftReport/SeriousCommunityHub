package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.UserAccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserAccessLogRepository extends JpaRepository<UserAccessLog,Long> {

    List<UserAccessLog> findAllByEmailOrderByAccessTimeDesc(String trim);
    Page<UserAccessLog> findAllByEmailOrderByAccessTimeDesc(String email, Pageable pageable);

    @Transactional
    void deleteAllByEmail(String email);
}
