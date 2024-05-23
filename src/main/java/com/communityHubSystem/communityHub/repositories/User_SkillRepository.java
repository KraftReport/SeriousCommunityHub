package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.User_Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface User_SkillRepository extends JpaRepository<User_Skill,Long>  {

    public List<User_Skill> findByUserId(Long id);

    Optional<User_Skill> findByUserIdAndSkillId(Long id, Long id1);

    User_Skill findBySkillId(Long id);
}
