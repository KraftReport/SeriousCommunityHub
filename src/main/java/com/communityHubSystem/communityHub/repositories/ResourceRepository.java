package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource,Long>, JpaSpecificationExecutor<Resource> {
    @Modifying
    @Transactional
    @Query(value = "delete from resource where post_id = :id",nativeQuery = true)
    void deleteByPostId(Long id);

    @Modifying
    @Transactional
    @Query(value = "delete from resource where id = :id",nativeQuery = true)
    void deleteWithId(Long id);

    @Query(value = "select * from resource where post_id = :id",nativeQuery = true)
    List<Resource> getResourcesByPostId(Long id);
}
