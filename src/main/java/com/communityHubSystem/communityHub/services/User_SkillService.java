package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.User_Skill;

import java.util.List;

public interface User_SkillService {

    void deleteSkillById(Long id);

    public List<User_Skill> findByUserId(Long id);

    void save(User_Skill user_skill);

    public User_Skill findBySkillId(Long id);
}
