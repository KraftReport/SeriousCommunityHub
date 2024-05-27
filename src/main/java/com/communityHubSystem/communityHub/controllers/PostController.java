package com.communityHubSystem.communityHub.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.communityHubSystem.communityHub.dto.FirstUpdateDto;
import com.communityHubSystem.communityHub.dto.PostDto;
import com.communityHubSystem.communityHub.dto.SecondUpdateDto;
import com.communityHubSystem.communityHub.dto.ViewPostDto;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.*;
import com.communityHubSystem.communityHub.repositories.PostRepository;
import com.communityHubSystem.communityHub.repositories.ResourceRepository;
import com.communityHubSystem.communityHub.services.CommunityService;
import com.communityHubSystem.communityHub.services.PostService;
import com.communityHubSystem.communityHub.services.UserService;
import com.communityHubSystem.communityHub.services.User_GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final ResourceRepository resourceRepository;
    private final Cloudinary cloudinary;
    private final UserService userService;
    private final User_GroupService user_groupService;
    private final CommunityService communityService;

    @PostMapping("/createPublicPost")
    public ResponseEntity<?> createPublicPost(@ModelAttribute PostDto publicPostDto,
                                              @RequestParam(value = "files", required = false) MultipartFile[] files,
                                              @RequestParam(value = "captions", required = false) String[] captions) throws IOException {
        System.out.println("publei " + publicPostDto);
        var post = postService.createPost(publicPostDto, files, captions);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    @PostMapping("/createARawFilePost")
    public ResponseEntity<?> createARawFilePost(@ModelAttribute PostDto postDto,
                                                @RequestParam(value = "rawFiles", required = false) MultipartFile[] rawFiles) throws IOException, ExecutionException, InterruptedException {
        System.err.println(rawFiles[0]);
        System.err.println(postDto + "this is postDto");
        return ResponseEntity.ok(postService.createRawFilePost(postDto, rawFiles));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Post>> getAllPost() {
        List<Post> posts = postService.findAllPost();
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }

    @GetMapping("/getPost/{id}")
    public ResponseEntity<Post> getPost(@PathVariable("id") String id) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.findById(Long.parseLong(id)));
    }

    @GetMapping("/deletePost/{id}")
    public ResponseEntity<?> deletePost(@PathVariable("id") String id) {
        postService.deletePost(Long.parseLong(id));
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @GetMapping("/searchPost/{input}")
    public ResponseEntity<List<Post>> searchMethod(@PathVariable("input") String input) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.searchMethod(input));
    }

    @PostMapping("/firstUpdate")
    public ResponseEntity<Post> firstUpdate(@ModelAttribute FirstUpdateDto firstUpdateDto,
                                            @RequestParam(value = "files", required = false) MultipartFile[] files,
                                            @RequestParam(value = "captions", required = false) String[] captions) throws IOException {
        System.err.println(firstUpdateDto + "----->controller");
        return ResponseEntity.status(HttpStatus.OK).body(postService.firstUpdate(firstUpdateDto, files, captions));
    }

    @PostMapping("/secondUpdate")
    public ResponseEntity<Post> secondUpdate(@RequestBody List<SecondUpdateDto> secondUpdateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.secondUpdate(secondUpdateDto));
    }

    @PostMapping("/firstUpdateRaw")
    public ResponseEntity<Post> firstUpdateRaw(@ModelAttribute FirstUpdateDto firstUpdateDto,
                                               @RequestParam(value = "files",required = false)MultipartFile[] files) throws IOException {
        return ResponseEntity.status(HttpStatus.OK).body(postService.firstUpdateRaw(firstUpdateDto,files));
    }

    @PostMapping("/secondUpdateRaw")
    public ResponseEntity<Post> secondUpdateRaw(@RequestBody List<SecondUpdateDto> secondUpdateDto){
        return ResponseEntity.status(HttpStatus.OK).body(postService.secondUpdateRaw(secondUpdateDto));
    }


    @GetMapping("/fivePost/{page}")
    public ResponseEntity<List<Post>> getTenPosts(@PathVariable("page") String page) {
        Page<Post> posts = postService.findPostRelatedToUser(page);
        System.out.println("ALL OBject Size" + posts.getContent().size());
        return ResponseEntity.ok(posts.getContent());
    }

    @GetMapping("/checkPostOwnerOrAdmin/{id}")
    @ResponseBody
    public ResponseEntity<List<Object>> checkPostOwnerOrAdmin(@PathVariable("id") String id) {
        return ResponseEntity.ok(postService.checkPostOwnerOrAdmin(Long.valueOf(id)));
    }

    @GetMapping("/getPostsForUserDetailPage/{id}/{page}")
    @ResponseBody
    public ResponseEntity<List<Post>> getPostsForUserDetailPage(@PathVariable("id") String id,
                                                                @PathVariable("page") String page) {
        System.err.println(postService.returnPostForUserDetailPage(Long.valueOf(id), page).getContent());
        return ResponseEntity.ok(postService.returnPostForUserDetailPage(Long.valueOf(id), page).getContent());
    }



    @GetMapping("/fetch-post/{id}")
    @ResponseBody
    public ResponseEntity<Post> getPostById(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.findById(id));
    }

    @GetMapping("/get-postWithUrl")
    @ResponseBody
    public ResponseEntity<?> getPostByUrl(@RequestParam("url") String url) {
        var loginUser = userService.findByStaffId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CommunityHubException("User Name Not Found Exception!"));

        var post = postService.findByUrl(url);

        if (post == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Post cannot be found anymore!");
        }

        if (loginUser.getRole().equals(User.Role.ADMIN) || post.getAccess().equals(Access.PUBLIC)) {
            return ResponseEntity.status(HttpStatus.OK).body(post);
        }

        var userGroup = user_groupService.findById(post.getUserGroup().getId());

        var community = communityService.findById(userGroup.getCommunity().getId());

        if(community.getGroupAccess().equals(Community.GroupAccess.PUBLIC)){
            return ResponseEntity.status(HttpStatus.OK).body(post);
        }

        List<User_Group> userGroups = user_groupService.findByCommunityId(userGroup.getCommunity().getId());

        List<String> userList = userGroups.stream()
                .map(User_Group::getUser)
                .map(User::getId)
                .map(userService::findById)
                .filter(user -> user != null && user.isActive())
                .map(User::getStaffId)
                .collect(Collectors.toList());

        if (userList.contains(loginUser.getStaffId())) {
            return ResponseEntity.status(HttpStatus.OK).body(post);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("You have no permission to do that!");
        }
    }

    @GetMapping("/singlePost/{id}")
    public ResponseEntity<Post> getSinglePost(@PathVariable("id")Long id){
        var post = postService.findById(id);
        if(post != null){
            if(!post.isDeleted()){
                return ResponseEntity.status(HttpStatus.OK).body(post);
            }else{
                return ResponseEntity.ok(null);
            }
        }else{
            return ResponseEntity.ok(null);
        }
    }

}
