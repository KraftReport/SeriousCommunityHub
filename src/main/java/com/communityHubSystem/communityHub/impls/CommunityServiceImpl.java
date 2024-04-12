package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.Community;
import com.communityHubSystem.communityHub.models.User;
import com.communityHubSystem.communityHub.models.User_Group;
import com.communityHubSystem.communityHub.repositories.CommunityRepository;
import com.communityHubSystem.communityHub.repositories.UserRepository;
import com.communityHubSystem.communityHub.repositories.User_GroupRepository;
import com.communityHubSystem.communityHub.services.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final User_GroupRepository user_groupRepository;


    @Override
    public void createCommunity(Community community, Long id) {
        MultipartFile file = community.getFile();
        var user = userRepository.findById(id).orElseThrow();
        community.setOwnerName(user.getName());
        community.setActive(true);
        if (file != null) {
            try {
                byte[] photoByte = file.getBytes();
                community.setImage(photoByte);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        var com = communityRepository.save(community);
        User_Group user_group = new User_Group();
        user_group.setUser(user);
        user_group.setCommunity(com);
        user_groupRepository.save(user_group);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public List<Community> getAllCommunity(Model model) {
        List<Community> communities = communityRepository.findAll();
        Map<Long, String> photoMap = new HashMap<>();
        for (Community community : communities) {
            if (community.getImage() != null) {
                String encodedString = Base64.getEncoder().encodeToString(community.getImage());
                photoMap.put(community.getId(), encodedString);
            }
        }
        model.addAttribute("photoMap", photoMap);
        return communities;
    }

    @Override
    public Community getCommunityBy(Long id) {
        return communityRepository.findById(id).orElseThrow();
    }

    @Override
    public void createGroup(Community community, List<Long> ids) {
        communityRepository.findById(community.getId()).ifPresent(c -> {
            c.setName(community.getName());
            c.setDescription(community.getDescription());
            MultipartFile file = community.getFile();
            if (file != null) {
                try {
                    byte[] photoByte = file.getBytes();
                    community.setImage(photoByte);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            communityRepository.save(c);
        });

        for (Long u_id : ids) {
            User_Group user_group = new User_Group();
            User user = userRepository.findById(u_id).orElseThrow();
            user_group.setCommunity(community);
            user_group.setUser(user);
            user_groupRepository.save(user_group);
        }
    }

    @Override
    public List<String> getOwnerNamesByCommunityId(Long communityId) {
        List<User_Group> userGroups = user_groupRepository.findByCommunityId(communityId);
        return userGroups.stream().map(userGroup -> userGroup.getUser().getName()).collect(Collectors.toList());
    }

    @Override
    public void kickGroup(Community community, List<Long> ids) {
        communityRepository.findById(community.getId()).ifPresent(c -> {

            for (Long u_id : ids) {
                user_groupRepository.deleteByCommunityIdAndUserId(community.getId(), u_id);
            }

        });
    }

    @Override
    public Community getCommunityById(Long communityId) {
    return communityRepository.findById(communityId).orElse(null);
    }

    @Override
    public List<Community> findAll() {
        return communityRepository.findAll();
    }

    @Override
    public List<Community> findAllByIsActive() {
        List<Community> communityList = communityRepository.findAll();
        List<Community> communities = new ArrayList<>();
        for(Community community:communityList){
            if(community.isActive() == true){
                communities.add(community);
            }
        }
        return communities;
    }


}
