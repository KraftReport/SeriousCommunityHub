package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.Community;
import com.communityHubSystem.communityHub.models.User_Group;

import java.util.List;

public interface User_GroupService  {

    public List<User_Group> findByCommunityId(Long id);

    List<User_Group> findByUserId(Long id);

   public User_Group findByUserIdAndCommunityId(Long id, Long id1);

    void save(User_Group groupUser);

    Community getCommunityByUserGroupIdAndUserId(Long userGroupId, Long userId);

    public User_Group findById(Long id);
}
