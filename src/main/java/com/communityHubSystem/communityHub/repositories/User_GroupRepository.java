package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.User_Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface User_GroupRepository extends JpaRepository<User_Group,Long> {

    List<User_Group> findByCommunityId(Long communityId);
    List<User_Group> findByUserId(Long userId);
    @Transactional
    @Modifying
    @Query("DELETE FROM User_Group ug WHERE ug.community.id = :communityId AND ug.user.id = :userId")
    void deleteByCommunityIdAndUserId(@Param("communityId") Long communityId, @Param("userId") Long userId);

    @Query (value = "select distinct community_id from user_group where user_id = :userId",nativeQuery = true)
    List<Long> findDistinctCommunityIdByUserId(Long userId);

    @Query(value = "select user_id from user_group where community_id = :communityId",nativeQuery = true)
    List<Long> getUserIdsFromCommunityId(Long communityId);

    @Query(value = "select community_id from user_group where user_id = :id",nativeQuery = true)
    List<Long> getCommunityIdFromUserId(Long id);

    @Query(value = "select id from user_group where community_id = :communityId",nativeQuery = true)
    List<Long> getIdFromCommunityId(Long communityId);

    User_Group findByUserIdAndCommunityId(Long id,Long userGroupId);


    @Query(value = "select * from user_group where id = : userGroupId and user_id = : userId",nativeQuery = true)
    Long findCommunityIdByUserGroupIdAndUserId(Long userGroupId,Long userId);
}
