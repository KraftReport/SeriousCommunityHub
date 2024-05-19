package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.models.UserAccessLog;
import com.communityHubSystem.communityHub.repositories.UserAccessLogRepository;
import com.communityHubSystem.communityHub.services.UserAccessLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAccessLogImpl implements UserAccessLogService {

    private final UserAccessLogRepository userAccessLogRepository;

    @Override
    public List<UserAccessLog> findAllByEmail(String email) {
        return userAccessLogRepository.findAllByEmailOrderByAccessTimeDesc(email.trim());
    }

    @Override
    public Page<UserAccessLog> findLogsByEmail(String email, Pageable pageable) {
        return userAccessLogRepository.findAllByEmailOrderByAccessTimeDesc(email.trim(), pageable);
    }

}
