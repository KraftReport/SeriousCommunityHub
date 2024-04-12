package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.models.React;
import com.communityHubSystem.communityHub.repositories.ReactRepository;
import com.communityHubSystem.communityHub.services.ReactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactServiceImpl implements ReactService {

    private final ReactRepository reactRepository;

    @Transactional
    @Override
    public React save(React react) {
        return reactRepository.save(react);
    }

    @Override
    public boolean findByPostIdAndUserId(Long id, Long id1) {
        var react = reactRepository.findByPostIdAndUserId(id,id1);
        if(react != null){
            return true;
        }
        return false;
    }

    @Override
    public List<React> findByPostId(Long id) {
        return reactRepository.findByPostId(id);
    }
}
