package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.Community;
import com.communityHubSystem.communityHub.models.User_Group;
import com.communityHubSystem.communityHub.repositories.CommunityRepository;
import com.communityHubSystem.communityHub.repositories.UserRepository;
import com.communityHubSystem.communityHub.repositories.User_GroupRepository;
import com.communityHubSystem.communityHub.services.User_GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class User_GroupImpl implements User_GroupService {

    private final User_GroupRepository user_groupRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;

    @Override
    public List<User_Group> findByCommunityId(Long id) {
        return user_groupRepository.findByCommunityId(id);
    }

    @Override
    public List<User_Group> findByUserId(Long id) {
        return user_groupRepository.findByUserId(id);
    }

    @Override
    public User_Group findByUserIdAndCommunityId(Long id, Long id1) {
        return user_groupRepository.findByUserIdAndCommunityId(id,id1);
    }

    @Transactional
    @Override
    public void save(User_Group groupUser) {
        user_groupRepository.save(groupUser);
    }

    @Override
    public Community getCommunityByUserGroupIdAndUserId(Long userGroupId, Long userId) {
        return communityRepository.findById(user_groupRepository.findCommunityIdByUserGroupIdAndUserId(userGroupId,userId)).orElseThrow(()->new CommunityHubException("community not found"));
    }

    @Override
    public User_Group findById(Long id) {
        return user_groupRepository.findById(id).orElseThrow(() -> new CommunityHubException("Group Not found Exception!"));
    }


}
