package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.models.React;
import com.communityHubSystem.communityHub.repositories.ReactRepository;
import com.communityHubSystem.communityHub.services.ReactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReactServiceImpl implements ReactService {

    private final ReactRepository reactRepository;

    @Override
    public React save(React react) {
        return reactRepository.save(react);
    }
}
