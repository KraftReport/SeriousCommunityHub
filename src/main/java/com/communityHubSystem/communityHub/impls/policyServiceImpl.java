package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.repositories.PolicyRepository;
import com.communityHubSystem.communityHub.services.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class policyServiceImpl implements PolicyService {
    @Autowired
    private PolicyRepository policyRepository;

    @Override
    public boolean policyExistsForUser(Long userId) {
        return policyRepository.existsByUserId(userId);
    }
}
