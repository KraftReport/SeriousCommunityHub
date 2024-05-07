package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.Community;
import com.communityHubSystem.communityHub.models.User;
import com.communityHubSystem.communityHub.models.User_ChatRoom;
import com.communityHubSystem.communityHub.models.User_Group;
import com.communityHubSystem.communityHub.repositories.CommunityRepository;
import com.communityHubSystem.communityHub.repositories.UserRepository;
import com.communityHubSystem.communityHub.repositories.User_GroupRepository;
import com.communityHubSystem.communityHub.services.ChatRoomService;
import com.communityHubSystem.communityHub.services.CommunityService;
import com.communityHubSystem.communityHub.services.ImageUploadService;
import com.communityHubSystem.communityHub.services.User_ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
    private final ChatRoomService chatRoomService;
    private final User_ChatRoomService user_chatRoomService;
    private final ImageUploadService imageUploadService;


    @Transactional
    @Override
    public Community createCommunity(MultipartFile file,Community community, Long id) throws IOException {
        var user = userRepository.findById(id).orElseThrow();
        community.setOwnerName(user.getName());
        community.setActive(true);
        if (file != null) {
            String imageUrl = imageUploadService.uploadImage(file);
            community.setImage(imageUrl);
        }
        Community com = communityRepository.save(community);
        User_Group user_group = new User_Group();
        user_group.setUser(user);
        user_group.setCommunity(com);
        user_groupRepository.save(user_group);
        return com;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

//    @Override
//    public List<Community> getAllCommunity(Model model) {
//        List<Community> communities = communityRepository.findAll();
//        Map<Long, String> photoMap = new HashMap<>();
//        for (Community community : communities) {
//            if (community.getImage() != null) {
//                String encodedString = Base64.getEncoder().encodeToString(community.getImage());
//                photoMap.put(community.getId(), encodedString);
//            }
//        }
//        model.addAttribute("photoMap", photoMap);
//        return communities;
//    }

    @Override
    public Community getCommunityBy(Long id) {
        return communityRepository.findById(id).orElseThrow(() -> new CommunityHubException("Community not found exception!"));
    }

    @Transactional
    @Override
    public void createGroup(MultipartFile file,Community community, List<Long> ids) {
        communityRepository.findById(community.getId()).ifPresent(c -> {
            c.setName(community.getName());
            c.setDescription(community.getDescription());
            if (file != null && !file.isEmpty()) {

                try {
                    String imageUrl = imageUploadService.uploadImage(file);
                    community.setImage(imageUrl);
                    c.setImage(community.getImage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Community existingCommunity = communityRepository.findById(community.getId()).orElse(null);
                if (existingCommunity != null) {
                    community.setImage(existingCommunity.getImage());
                    c.setImage(community.getImage());
                }
            }
            communityRepository.save(c);
        });
        if (ids.size() > 0) {
            for (Long u_id : ids) {
                User_Group user_group = new User_Group();
                User user = userRepository.findById(u_id).orElseThrow();
                user_group.setCommunity(community);
                user_group.setUser(user);
                user_groupRepository.save(user_group);
            }
            var chatRoom = chatRoomService.findByCommunityId(community.getId());
           var userIds = new ArrayList<>();
            var user_chatRoom = user_chatRoomService.findByChatRoomId(chatRoom.getId());
             if(user_chatRoom != null){
                 for(User_ChatRoom user_chatRoom1:user_chatRoom){
                    userIds.add(user_chatRoom1.getUser().getId());
                 }
                 if(!userIds.contains(ids)){
                     for (Long u_id : ids) {
                    var user = userRepository.findById(u_id).orElseThrow(() -> new CommunityHubException("User name not found exception!"));
                    var user_chat_room= User_ChatRoom.builder()
                            .date(new Date())
                            .user(user)
                            .chatRoom(chatRoom)
                            .build();
                    user_chatRoomService.save(user_chat_room);
                     }
                 }
             }

        }
    }

    @Override
    public List<String> getOwnerNamesByCommunityId(Long communityId) {
        List<User_Group> userGroups = user_groupRepository.findByCommunityId(communityId);
        return userGroups.stream().map(userGroup -> userGroup.getUser().getName()).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void kickGroup(Community community, List<Long> ids) {
        communityRepository.findById(community.getId()).ifPresent(c -> {
            for (Long u_id : ids) {
                user_groupRepository.deleteByCommunityIdAndUserId(community.getId(), u_id);
            }
        });
        var chatRoom = chatRoomService.findByCommunityId(community.getId());
        for (Long id:ids){
            var user_chat_room = user_chatRoomService.findByUserIdAndChatRoomId(id,chatRoom.getId());
            user_chatRoomService.deleteById(user_chat_room.getId());
        }
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
        for (Community community : communityList) {
            if (community.isActive() == true) {
                communities.add(community);
            }
        }
        return communities;
    }

    @Override
    public List<Community> getAllCommunityWithUserId() {
        return validateUserOrAdmin(getLoginUserCommunities());
    }

    @Override
    public List<Community> communitySearchMethod(String input) {
        var specification = new ArrayList<Specification<Community>>();
        if (StringUtils.hasLength(input)) {
            specification.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%".concat(input.toLowerCase()).concat("%")));
            specification.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%".concat(input.toLowerCase()).concat("%")));
            specification.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("ownerName")), "%".concat(input.toLowerCase()).concat("%")));
        }
        Specification<Community> communitySpecification = Specification.where(null);
        for (var s : specification) {
            communitySpecification = communitySpecification.or(s);
        }
        return communityRepository.findAll(communitySpecification);
    }

    @Override
    public boolean existsByName(String name) {
        return communityRepository.existsByName(name);
    }

    private List<Community> validateUserOrAdmin(List<Community> communities) {
        if (getLoginUser().getRole().equals(User.Role.ADMIN)) {
            return communityRepository.findAll();
        } else {
            return communities;
        }
    }

    private List<Community> getLoginUserCommunities() {
        var ids = user_groupRepository.findDistinctCommunityIdByUserId(getLoginUser().getId());
        var communities = new ArrayList<Community>();
        for (var id : ids) {
            communities.add(communityRepository.findById(id).orElseThrow(() -> new CommunityHubException("not found group")));
        }
        return communities;
    }

    private User getLoginUser() {
        return userRepository.findByStaffId(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new CommunityHubException("user not found"));
    }


}
