package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PollRepository extends JpaRepository<Poll,Long>, JpaSpecificationExecutor<Poll> {
}
