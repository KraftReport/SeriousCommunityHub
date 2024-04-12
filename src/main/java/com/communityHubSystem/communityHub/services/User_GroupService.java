package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.User_Group;

import java.util.List;

public interface User_GroupService  {

    public List<User_Group> findByCommunityId(Long id);

    List<User_Group> findByUserId(Long id);
}
