package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Event;
import com.communityHubSystem.communityHub.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event,Long>, JpaSpecificationExecutor<Event> {

    List<Event> findByEventType(Event.EventType eventType);

    @Query(value = "select * from event where created_date between ?1 and ?2 order by created_date asc",nativeQuery = true)
    List<Event> findByCreatedDateBetween(Date startDate, Date endDate);

    @Query(value = "select * from event where user_group_id = :id",nativeQuery = true)
    List<Event> findByUserGroupId(Long id);

    @Query(value = "select * from event where user_group_id = :id ",nativeQuery = true)
    List<Event> getEventsForCommunityDetailPage(Long id);

    @Query(value="select * from event where title = :s ",nativeQuery = true)
    Event findByEventTitle(String s);

    @Query("SELECT e FROM Event e WHERE e.title = :title AND e.start_date = :startDate")
    Optional<Event> findByEventTitleAndStartDate(@Param("title") String title, @Param("startDate") Date startDate);
}
