package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long>, JpaSpecificationExecutor<Event> {

    List<Event> findByEventType(Event.EventType eventType);

    @Query(value = "select * from event where created_date between ?1 and ?2 order by created_date asc",nativeQuery = true)
    List<Event> findByCreatedDateBetween(Date startDate, Date endDate);

    @Query(value = "select * from event where user_group_id = :id",nativeQuery = true)
    List<Event> findByUserGroupId(Long id);

    @Query(value = "select * from event where user_group_id = :id ",nativeQuery = true)
    List<Event> getEventsForCommunityDetailPage(Long id);

}
