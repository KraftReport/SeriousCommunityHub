package com.communityHubSystem.communityHub.controllers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.communityHubSystem.communityHub.dto.FirstUpdateDto;
import com.communityHubSystem.communityHub.dto.PostDto;
import com.communityHubSystem.communityHub.dto.SecondUpdateDto;
import com.communityHubSystem.communityHub.dto.ViewPostDto;
import com.communityHubSystem.communityHub.models.Post;
import com.communityHubSystem.communityHub.repositories.PostRepository;
import com.communityHubSystem.communityHub.repositories.ResourceRepository;
import com.communityHubSystem.communityHub.services.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final ResourceRepository resourceRepository;
    private final Cloudinary cloudinary;

    @PostMapping("/createPublicPost")
    public ResponseEntity<?> createPublicPost(@ModelAttribute PostDto publicPostDto,
                                              @RequestParam(value = "files",required = false) MultipartFile[] files,
                                              @RequestParam(value = "captions",required = false) String[] captions) throws IOException {
        System.out.println("publei "+publicPostDto);
        var post = postService.createPost(publicPostDto, files, captions);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    @PostMapping("/createARawFilePost")
    public ResponseEntity<?> createARawFilePost(@RequestParam("rawFiles")MultipartFile[] rawFiles) throws IOException {
        System.err.println(rawFiles[0]);
        for (var r : rawFiles){
            Map uploadResult = cloudinary.uploader()
                    .upload(r.getBytes(), ObjectUtils.asMap(
                            "resource_type", "raw",
                            "public_id", UUID.randomUUID().toString()
                    ));

            System.err.println(uploadResult.get("url").toString());
        }


        return ResponseEntity.ok(null);

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


    @GetMapping("/fivePost/{page}")
    public ResponseEntity<List<Post>> getTenPosts(@PathVariable("page") String page) {
        Page<Post> posts = postService.findPostRelatedToUser(page);
        System.out.println("ALL OBject Size"+posts.getContent().size());
        return ResponseEntity.ok(posts.getContent());
    }

    @GetMapping("/checkPostOwnerOrAdmin/{id}")
    @ResponseBody
    public ResponseEntity<List<Object>> checkPostOwnerOrAdmin(@PathVariable("id")String id){
        return ResponseEntity.ok(postService.checkPostOwnerOrAdmin(Long.valueOf(id)));
    }

    @GetMapping("/getPostsForUserDetailPage/{id}/{page}")
    @ResponseBody
    public ResponseEntity<List<Post>> getPostsForUserDetailPage(@PathVariable("id")String id,
                                                                @PathVariable("page")String page){
        return ResponseEntity.ok(postService.returnPostForUserDetailPage(Long.valueOf(id),page).getContent());
    }
    @GetMapping("/fetch-post/{id}")
    @ResponseBody
    public ResponseEntity<Post> getPostById(@PathVariable("id")Long id){
        return ResponseEntity.status(HttpStatus.OK).body(postService.findById(id));
    }


}
