package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.dto.PostDto;
import com.communityHubSystem.communityHub.models.Post;
import com.communityHubSystem.communityHub.repositories.PostRepository;
import com.communityHubSystem.communityHub.repositories.ResourceRepository;
import com.communityHubSystem.communityHub.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {
    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    ResourceRepository resourceRepository;
    @PostMapping("/createPublicPost")
    public ResponseEntity<?> createPublicPost(@ModelAttribute PostDto publicPostDto, @RequestParam("files")MultipartFile[] files, @RequestParam("captions")String[] captions) throws IOException {
        var  post =  postService.createPost(publicPostDto,files,captions);
        return ResponseEntity.status(HttpStatus.OK).body(post);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Post>> getAllPost(){
        List<Post> posts = postService.findAllPost();
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }
}
