package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.dto.GroupAccessChangeDto;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.*;
import com.communityHubSystem.communityHub.repositories.*;
import com.communityHubSystem.communityHub.services.ChatRoomService;
import com.communityHubSystem.communityHub.services.CommunityService;
import com.communityHubSystem.communityHub.services.ImageUploadService;
import com.communityHubSystem.communityHub.services.User_ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final PostRepository postRepository;
    private final EventRepository eventRepository;


    @Transactional
    @Override
    public Community createCommunity(MultipartFile file, Community community, Long id) throws IOException {
        var user = userRepository.findById(id).orElseThrow();
        community.setOwnerName(user.getName());
        community.setActive(true);
        community.setDate(new Date());

        if (file != null && !file.isEmpty()) {
            String imageUrl = imageUploadService.uploadImage(file);
            community.setImage(imageUrl);
        } else {
            String defaultUrl = "/assets/img/default-logo.png";
            community.setImage(defaultUrl);
        }

        Community com = communityRepository.save(community);
        User_Group user_group = new User_Group();
        user_group.setUser(user);
        user_group.setDate(new Date());
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
                user_group.setDate(new Date());
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
        List<User> users = new ArrayList<>();
        for(User_Group user_group:userGroups){
            var user = userRepository.findById(user_group.getUser().getId()).orElseThrow(() -> new CommunityHubException("User not found exception!"));
            if(!user.getRole().equals(User.Role.ADMIN)){
                users.add(user);
            }
        }
        return users.stream().map(User::getName).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void kickGroup(Community community, List<Long> ids) {
        communityRepository.findById(community.getId()).ifPresent(c -> {
            for (Long u_id : ids) {
                var user = userRepository.findById(u_id).orElseThrow(() -> new CommunityHubException("User  not found exception!"));
                var isExisted = communityRepository.findByIdAndOwnerName(community.getId(),user.getName().trim());
                if(isExisted != null){
                    System.out.println("A SIN PYAE TAL");
                    isExisted.setOwnerName("ANONYMOUS");
                    communityRepository.save(isExisted);
                }
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
    public List<Community> communitySearchMethod(String in) {
        var input = URLDecoder.decode(in, StandardCharsets.UTF_8);
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
        return communityRepository.findAll(communitySpecification).stream().filter(c->c.getGroupAccess().equals(Community.GroupAccess.PUBLIC)).toList();
    }

    @Override
    public Object getNumberOfUsersOfACommunity(Long id) {
        var list = user_groupRepository.getUserIdsFromCommunityId(id);
        var objs = new ArrayList<Object>();
        objs.add(list.size()+"");
        return objs;
    }

    @Override
    public boolean existsByName(String name) {
        return communityRepository.existsByName(name);
    }

    @Override
    public Page<Post> getPostsForCommunityDetailPage(Long communityId,String page) {
//        var userGroupIds = user_groupRepository.getIdFromCommunityId(communityId);
//        return userGroupIds.stream().flatMap(u->postRepository.findAllByUserGroupId(u).stream()).toList();
        return fetchPostsForCommunity(communityId,page);
    }

    @Override
    public Page<Event> getEventsForCommunityDetailPage(Long aLong,String page) {
        return fetchEventsForCommunity(aLong,page, Event.EventType.EVENT);
    }

    @Override
    public Page<Event> getPollsForCommunityDetailPage(Long aLong,String page) {
        return fetchEventsForCommunity(aLong,page, Event.EventType.VOTE);
    }

    @Override
    public Community findByIdAndOwnerName(Long id, String name) {
        return communityRepository.findByIdAndOwnerName(id,name);
    }

    @Transactional
    @Override
    public void svgOwner(Long communityId, Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new CommunityHubException("User not found!"));

        var community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityHubException("Community not found!"));

        community.setOwnerName(user.getName());
        var userGroup = User_Group.builder()
                .date(new Date())
                .user(user)
                .community(community)
                .build();
        communityRepository.save(community);
        user_groupRepository.save(userGroup);
    }

    @Transactional
    @Override
    public void changeAccess(GroupAccessChangeDto groupAccessChangeDto) {
        communityRepository.findById(groupAccessChangeDto.getCommunityId()).ifPresentOrElse(community -> {
            Community.GroupAccess newAccess = groupAccessChangeDto.getGroupAccess() == Community.GroupAccess.PUBLIC
                    ? Community.GroupAccess.PRIVATE
                    : Community.GroupAccess.PUBLIC;
            community.setGroupAccess(newAccess);
            communityRepository.save(community);
        }, () -> {
            throw new CommunityHubException("Community not found with id: " + groupAccessChangeDto.getCommunityId());
        });
    }

    @Override
    public Community findById(Long id) {
        return communityRepository.findById(id).orElseThrow(() -> new CommunityHubException("Community not found exception!"));
    }

    @Override
    public List<User> getMembersOfACommunity(Long communityId) {
        var userIds = user_groupRepository.findUserIdByCommunityId(communityId);
        return userIds.stream()
                .map(s->userRepository
                        .findById(s)
                        .orElseThrow(()->new CommunityHubException("user not found")))
                .toList();
    }

    @Override
    public User getOwnerOfTheCommunity(Long communityId) {
        var community = communityRepository.findById(communityId).orElseThrow(()->new CommunityHubException("community not found"));
        return userRepository.findByName(community.getOwnerName());
    }

    @Transactional
    @Override
    public void modifyStatus(Long id) {
        communityRepository.findById(id).ifPresent(c -> {
            c.setActive(true);
            communityRepository.save(c);
        });
       var chatRoom = chatRoomService.findByCommunityId(id);
       if(chatRoom != null){
           chatRoom.setDeleted(true);
       }
       chatRoomService.saveChatRoom(chatRoom);
    }

    @Transactional
    @Override
    public void leaveFromGroup(Long id) {
        var user = userRepository.findByStaffId(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new CommunityHubException("User name not found exception!"));
        User_Group userGroup = user_groupRepository.findByUserIdAndCommunityId(user.getId(),id);
        if(userGroup != null){
            System.out.println("SUCCESS==>");
            System.out.println("SDFDSFDSF==>" + userGroup.getCommunity().getName());
            System.out.println("SDFDSFDSF==>" + userGroup.getUser().getName());
            try {
                user_groupRepository.deleteByCommunityIdAndUserId(id, user.getId());
                System.out.println("Deletion successful");
            } catch (Exception e) {
                System.out.println("Error during deletion: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    public Page<Event> fetchEventsForCommunity(Long id, String page, Event.EventType eventType) {
        List<Event> postList = new ArrayList<>();
        var now = LocalDateTime.now();
        List<User_Group> userGroupList = user_groupRepository.findByCommunityId(id);
        for (User_Group user_group : userGroupList) {
            List<Event> userGroupPosts = eventRepository.findByUserGroupId(user_group.getId());
            var filteredList = userGroupPosts.stream().filter(u -> !u.isDeleted() &&
                    u.getEventType().equals(eventType) &&
                    u.getEnd_date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().isAfter(now)).toList();
            System.err.println("WOWOOWOWOWO"+user_group.getId());
            postList.addAll(filteredList);
        }
        postList.sort(Comparator.comparing(Event::getCreated_date).reversed());

        Pageable pageable = PageRequest.of(Integer.parseInt(page), 5);
        int start = Math.toIntExact(pageable.getOffset());
        if (start >= postList.size()) {
            return Page.empty(pageable);
        }
        int end = Math.min(start + pageable.getPageSize(), postList.size());
        List<Event> paginatedPosts = postList.subList(start, end);
        Page<Event> postPage = new PageImpl<>(paginatedPosts, pageable, postList.size());
        return postPage;
    }

    public Page<Post> fetchPostsForCommunity(Long id,String page) {
        List<Post> postList = new ArrayList<>();
        List<User_Group> userGroupList = user_groupRepository.findByCommunityId(id);
        for (User_Group user_group : userGroupList) {
            List<Post> userGroupPosts = postRepository.findAllByUserGroupId(user_group.getId());
            var filteredList = userGroupPosts.stream().filter(u -> !u.isDeleted() && !u.isHide()).toList();
            System.err.println("WOWOOWOWOWO"+user_group.getId());
            postList.addAll(filteredList);
        }
        postList.sort(Comparator.comparing(Post::getCreatedDate).reversed());

        Pageable pageable = PageRequest.of(Integer.parseInt(page), 5);
        int start = Math.toIntExact(pageable.getOffset());
        if (start >= postList.size()) {
            return Page.empty(pageable);
        }
        int end = Math.min(start + pageable.getPageSize(), postList.size());
        List<Post> paginatedPosts = postList.subList(start, end);
        Page<Post> postPage = new PageImpl<>(paginatedPosts, pageable, postList.size());
        return postPage;
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
