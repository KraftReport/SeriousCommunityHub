package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityRepository extends JpaRepository<Community,Long>, JpaSpecificationExecutor<Community> {

    boolean existsByName(String name);


    @Query(value = "select * from community where owner_name = :name",nativeQuery = true)
    Community findCommunityByOwnerName(String name);

    @Query("SELECT COUNT(c) FROM Community c WHERE c.ownerName = :ownerName")
    long countCommunitiesByOwnerName(@Param("ownerName") String ownerName);
    Community findByIdAndOwnerName(Long id, String name);
}
