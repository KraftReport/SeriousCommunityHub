package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.Mention;
import com.communityHubSystem.communityHub.repositories.MentionRepository;
import com.communityHubSystem.communityHub.services.MentionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MentionImpl implements MentionService {

    private final MentionRepository mentionRepository;

    @Transactional
    @Override
    public void save(Mention mention) {
        mentionRepository.save(mention);
    }

    @Override
    public Mention getMention(Long id) {
        return mentionRepository.findById(id).orElseThrow(() -> new CommunityHubException("Mention Not Found Exception!"));
    }
}
