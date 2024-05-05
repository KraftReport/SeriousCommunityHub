package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface VoteOptionRepository extends JpaRepository<VoteOption,Long>, JpaSpecificationExecutor<VoteOption> {

    @Modifying
    @Transactional
    @Query(value = "delete from vote_option where id = :id",nativeQuery = true)
    public void deleteByIdWithQuery(Long id);
}
