package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.models.User_Skill;
import com.communityHubSystem.communityHub.repositories.User_SkillRepository;
import com.communityHubSystem.communityHub.services.User_SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class User_SkillImpl implements User_SkillService {

    private  final User_SkillRepository user_skillRepository;


    @Override
    public void deleteSkillById(Long id) {
        var userSkill = user_skillRepository.findBySkillId(id);
        user_skillRepository.deleteById(userSkill.getId());
    }

    @Override
    public List<User_Skill> findByUserId(Long id) {
        return user_skillRepository.findByUserId(id);
    }

    @Override
    public void save(User_Skill user_skill) {
        user_skillRepository.save(user_skill);
    }

    @Override
    public User_Skill findBySkillId(Long id) {
        return user_skillRepository.findBySkillId(id);
    }
}
