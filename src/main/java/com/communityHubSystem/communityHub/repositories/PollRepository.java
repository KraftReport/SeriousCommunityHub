package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PollRepository extends JpaRepository<Poll,Long>, JpaSpecificationExecutor<Poll> {

    @Modifying
    @Query(value = "delete from poll where event_id = ?1 and user_id = ?2",nativeQuery = true)
    public void  deleteByEventIdAndUserId(Long eventId,Long userId);

    @Query(value = "select * from poll where vote_option_id = ?1",nativeQuery = true)
    public List<Poll> searchByVoteOptionId(Long voteOptionId);

    @Query(value = "select * from poll where user_id = ?2 and event_id = ?1",nativeQuery = true)
    public List<Poll> searchByEventIdAndUserId(Long eventId,Long userId);

    @Modifying
    @Transactional
    @Query(value = "delete from poll where event_id = ?1 and user_id = ?2",nativeQuery = true)
    public void deleteAPoll(Long eventId,Long userId);

    @Modifying
    @Transactional
    @Query(value = "delete from poll where vote_option_id = :id",nativeQuery = true)
    public void deleteByIdWithQuery(Long id);

    @Query(value = "select * from poll where user_id = ?1 and event_id = ?2",nativeQuery = true)
    public Poll searchByUserIdAndEventId(Long userId,Long eventId);
}
