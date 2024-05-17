package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Mention;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentionRepository extends JpaRepository<Mention,Long> {
}
