package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.Skill;
import com.communityHubSystem.communityHub.repositories.SkillRepository;
import com.communityHubSystem.communityHub.services.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    @Override
    public Skill findById(Long id) {
        return skillRepository.findById(id).orElseThrow(() -> new CommunityHubException("Skill not found Exception!"));
    }

    @Transactional
    @Override
    public Skill saveSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    @Override
    public Skill findByName(String name) {
        return skillRepository.findByName(name).orElse(null);
    }
}
