package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.dto.FirstUpdateDto;
import com.communityHubSystem.communityHub.dto.PostDto;
import com.communityHubSystem.communityHub.dto.SecondUpdateDto;
import com.communityHubSystem.communityHub.dto.ViewPostDto;
import com.communityHubSystem.communityHub.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.security.core.parameters.P;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    public Post createPost(PostDto postDTO, MultipartFile[] files, String[] captions) throws IOException;
    public List<Post> findAllPost();


    public Post findById(Long postId);

    public List<Post> findPostByUserId(Long id);


    public void deletePost(Long id);
    public List<Post> searchMethod(String input);
    public Post firstUpdate(FirstUpdateDto firstUpdateDto, MultipartFile[] files, String[] captions) throws IOException;
    public Post secondUpdate(List<SecondUpdateDto> secondUpdateDto);
    public List<Object> getFivePostsPerTime(String page);

   //public Page<ViewPostDto> findPostRelatedToUser(String page);


    public Page<Post> findPostRelatedToUser(String page);
    public List<Object> checkPostOwnerOrAdmin(Long id);
    public Page<Post> returnPostForUserDetailPage(Long id,String page);

   public List<Post> findAllPostByIsDeleted(boolean value,Long id);

   public List<Post> findAllPostByIsDeletedAndUserGroupId(Long id);
}
