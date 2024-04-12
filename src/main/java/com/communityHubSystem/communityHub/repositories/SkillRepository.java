package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Skill;
import com.communityHubSystem.communityHub.models.User_Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill,Long> , JpaSpecificationExecutor<User_Skill> {
    Optional<Skill> findByName(String skillName);
}
