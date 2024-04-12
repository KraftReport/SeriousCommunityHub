package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.dto.PostDto;
import com.communityHubSystem.communityHub.models.Post;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    public Post createPost(PostDto postDTO, MultipartFile[] files, String[] captions) throws IOException;
    public List<Post> findAllPost();



}
