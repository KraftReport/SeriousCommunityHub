package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.UserAccessLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserAccessLogService {

    public List<UserAccessLog> findAllByEmail(String email);

    Page<UserAccessLog> findLogsByEmail(String email, Pageable pageable);
}
