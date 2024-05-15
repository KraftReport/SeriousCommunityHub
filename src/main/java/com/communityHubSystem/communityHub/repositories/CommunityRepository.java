package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface CommunityRepository extends JpaRepository<Community,Long>, JpaSpecificationExecutor<Community> {

    boolean existsByName(String name);


    @Query(value = "select * from community where owner_name = :name",nativeQuery = true)
    Community findCommunityByOwnerName(String name);
}
