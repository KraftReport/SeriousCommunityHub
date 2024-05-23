package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.Skill;

public interface SkillService {

    public Skill findById(Long id);

    public Skill saveSkill(Skill skill);

    public Skill findByName(String trim);
}
