package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommunityRepository extends JpaRepository<Community,Long>, JpaSpecificationExecutor<Community> {

    boolean existsByName(String name);
}
