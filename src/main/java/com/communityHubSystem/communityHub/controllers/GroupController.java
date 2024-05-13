package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.models.*;
import com.communityHubSystem.communityHub.repositories.ChatRoomRepository;
import com.communityHubSystem.communityHub.repositories.CommunityRepository;
import com.communityHubSystem.communityHub.repositories.UserRepository;
import com.communityHubSystem.communityHub.repositories.User_GroupRepository;
import com.communityHubSystem.communityHub.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class GroupController {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final User_GroupRepository user_groupRepository;
    private final CommunityService communityService;
    private final User_GroupService user_groupService;
    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;
    private final User_ChatRoomService user_chatRoomService;
    private final ImageUploadService imageUploadService;
    private final ChatRoomService chatRoomService;

    @GetMapping("/group")
    public String group(Model model) {
        List<User> users = communityService.getAll();
        model.addAttribute("users", users);
        return "user/user-group";
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> user = userService.findByStaffId(staffId.trim());

        if (user.isPresent()) {
            if (User.Role.ADMIN.equals(user.get().getRole())) {
                List<User> users = userService.getAllUser().stream()
                        .filter(u -> !User.Role.ADMIN.equals(u.getRole()))
                        .collect(Collectors.toList());
                return ResponseEntity.status(HttpStatus.OK).body(users);
            }
            if (User.Role.USER.equals(user.get().getRole())) {
                List<User> users = userService.getAllUser().stream()
                        .filter(u -> !User.Role.ADMIN.equals(u.getRole()))
                        .collect(Collectors.toList());
                return ResponseEntity.status(HttpStatus.OK).body(users);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
    }

    @PostMapping("/createCommunity")
    public ResponseEntity<Map<String, String>> createGroup(@ModelAttribute Community community, @RequestParam("ownerId") Long ownerID,
                                                           @RequestParam("file")MultipartFile file) throws IOException {
        if (communityService.existsByName(community.getName())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Group name already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        var svgCommunity = communityService.createCommunity(file,community, ownerID);
        String photo = imageUploadService.uploadImage(file);
        var chatRoom = ChatRoom.builder()
                .date(new Date())
                .name(svgCommunity.getDescription())
                .photo(photo)
                .isDeleted(true)
                .community(svgCommunity)
                .build();
        chatRoomRepository.save(chatRoom);
        var user = userService.findById(ownerID);
        var user_chatRoom = User_ChatRoom.builder()
                .date(new Date())
                .user(user)
                .chatRoom(chatRoom)
                .build();
        user_chatRoomService.save(user_chatRoom);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Created successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("viewcommunity")
    public String views() {
        return "user/community-view";
    }


    @GetMapping("/communityview")
    @ResponseBody
    public List<Community> view() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> user = userService.findByStaffId(staffId.trim());
        if (user.isPresent()) {
            if (User.Role.ADMIN.equals(user.get().getRole())) {
                return communityService.findAllByIsActive();
            } else {
                List<User_Group> user_groups = user_groupRepository.findByUserId(user.get().getId());

                List<Community> communities = new ArrayList<>();

                for (User_Group user_group : user_groups) {
                    var community = communityService.getCommunityById(user_group.getCommunity().getId());
                    if(community.isActive()) {
                        communities.add(community);
                    }
                }

                return communities;
            }
        } else {
            return Collections.EMPTY_LIST;
        }
    }


    @PostMapping("/createGroup")
    public ResponseEntity<Map<String, String>> createCommunity(@ModelAttribute Community community, @RequestParam(required = false) Long[] user,@RequestParam("file")MultipartFile file) {
        List<Long> userList;
        if (user == null) {
            userList = Collections.emptyList();
        } else {
            userList = Arrays.asList(user);
        }
        communityService.createGroup(file,community, userList);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Created successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/ownerNames/{communityId}")
    public ResponseEntity<?> getOwnerNames(@PathVariable Long communityId) {
        try {
            List<String> ownerNames = communityService.getOwnerNamesByCommunityId(communityId);
            return ResponseEntity.ok(ownerNames);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching owner names");
        }
    }

    @DeleteMapping("/delete/{communityId}")
    public ResponseEntity<?> deleteUser(@PathVariable("communityId") Long communityId) {
        chatRoomService.deleteChatRoomByCommunityId(communityId);
        System.out.println("Deleting chat room with communityId: " + communityId);
        communityRepository.findById(communityId).ifPresent(community -> {
            community.setActive(false);
            communityRepository.save(community);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/user/current")
    public ResponseEntity<User> getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName();
        Optional<User> user = userService.findByStaffId(staffId.trim());
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{communityId}")
    public ResponseEntity<List<User>> getAllUsersByCommunity(@PathVariable("communityId") Long communityId, Model model) {
        var community = communityService.getCommunityBy(communityId);
        System.out.println("IDDIDI" + community.getId());
        List<User_Group> user_groups = user_groupService.findByCommunityId(communityId);
        List<User> users = new ArrayList<>();
        for (User_Group user_group : user_groups) {
            User user = userService.findById(user_group.getUser().getId());
            users.add(user);
        }
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PostMapping("/kick")
    public ResponseEntity<Map<String, String>> kickCommunity(@ModelAttribute Community community, @RequestParam("userIds") Long[] user) {
        communityService.kickGroup(community, Arrays.asList(user));
        Map<String, String> response = new HashMap<>();
        response.put("message", "Kicked successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("getCommunity/{communityId}")
    public ResponseEntity<Community> getCommunityById(@PathVariable Long communityId) {
        Community community = communityService.getCommunityById(communityId);
        if (community != null) {
            return ResponseEntity.ok(community);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/loginUserGroups")
    public ResponseEntity<List<Community>> getAllCommunities() {
        var staffId = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userService.findByStaffId(staffId).orElse(null);
        List<Community> communityList = new ArrayList<>();
        if (user != null) {
            if (User.Role.ADMIN.equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.OK).body(communityService.findAllByIsActive());
            } else {
                //need condition to check group is active or not
                List<User_Group> user_groups = user_groupService.findByUserId(user.getId());
                for (User_Group user_group : user_groups) {
                    var community = communityService.getCommunityById(user_group.getCommunity().getId());
                    if(community.isActive()) {
                        communityList.add(community);
                    }
                }
                return ResponseEntity.status(HttpStatus.OK).body(communityList);
            }
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(Collections.EMPTY_LIST);
        }
    }

    @GetMapping("/communitySearchMethod/{input}")
    @ResponseBody
    public ResponseEntity<List<Community>> communitySearchMethod(@PathVariable("input")String  input){
        return ResponseEntity.ok(communityService.communitySearchMethod(input));
    }

    @GetMapping("/getNumberOfMembers/{id}")
    @ResponseBody
    public ResponseEntity<Object> getNumberOfMembers(@PathVariable("id")String id){
        return ResponseEntity.ok(communityService.getNumberOfUsersOfACommunity(Long.valueOf(id)));
    }

    @GetMapping("/goToCommunityDetail")
    public String goToDetail(){
        return "/layout/group-post";
    }

    @GetMapping("/getPostsForCommunityDetailPage/{communityId}")
    @ResponseBody
    public ResponseEntity<List<Post>> getPosts(@PathVariable("communityId")String communityId){
     return ResponseEntity.ok(communityService.getPostsForCommunityDetailPage(Long.valueOf(communityId)));
    }

    @GetMapping("/getEventsForCommunityDetailPage/{communityId}")
    @ResponseBody
    public ResponseEntity<List<Event>> getEvents(@PathVariable("communityId")String communityId){
        return ResponseEntity.ok(communityService.getEventsForCommunityDetailPage(Long.valueOf(communityId)));
    }

    @GetMapping("/getPollsForCommunityDetailPage/{communityId}")
    @ResponseBody
    public ResponseEntity<List<Event>> getPolls(@PathVariable("communityId")String communityId){
        return ResponseEntity.ok(communityService.getPollsForCommunityDetailPage(Long.valueOf(communityId)));
    }

}
