package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.repositories.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface PolicyService {

    public boolean policyExistsForUser(Long userId);
}
