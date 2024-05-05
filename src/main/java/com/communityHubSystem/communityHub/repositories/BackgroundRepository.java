package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Background;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BackgroundRepository extends JpaRepository<Background,Long> {

    List<Background> findByUserId(Long userId);
}
