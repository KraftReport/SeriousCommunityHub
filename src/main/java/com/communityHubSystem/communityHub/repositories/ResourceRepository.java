package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource,Long> {
}
