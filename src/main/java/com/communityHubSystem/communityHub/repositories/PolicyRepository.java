package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PolicyRepository extends JpaRepository<Policy,Long> {
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Policy p WHERE p.user.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
}
