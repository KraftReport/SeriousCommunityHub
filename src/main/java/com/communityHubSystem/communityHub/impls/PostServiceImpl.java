package com.communityHubSystem.communityHub.impls;

import com.cloudinary.Cloudinary;
import com.communityHubSystem.communityHub.dto.PostDto;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.*;
import com.communityHubSystem.communityHub.repositories.*;
import com.communityHubSystem.communityHub.services.PostService;
import com.communityHubSystem.communityHub.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final CommunityRepository communityRepository;
    private final User_GroupRepository user_groupRepository;
    private final PollRepository pollRepository;
    private final PostRepository postRepository;
    private final Cloudinary cloudinary;
    private final ResourceRepository resourceRepository;
    private final UserService userService;
    private final List<String> photoExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", "bmp","tiff","tif","psv","svg","webp","ico","heic");
    private final List<String> videoExtensions = Arrays.asList(".mp4", ".avi", ".mov", ".wmv" ,"mkv" ,"flv","mpeg","mpg","webm","3gp","ts");




    @Override
    public Post createPost(PostDto postDTO, MultipartFile[] files, String[] captions) throws IOException {
        if(files.length > 0){
            return createResource(postDTO,files,captions);
        }else{
            return createCaption(postDTO);
        }

    }



    @Override
    public List<Post> findAllPost() {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdDate"));
    }

    @Override
    public Post findById(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }


    public boolean isValidPhotoExtension(String extension) {
        return photoExtensions.contains(extension);
    }

    private boolean isValidVideoExtension(String extension) {
        return videoExtensions.contains(extension);
    }



    private Post createResource(PostDto postDTO, MultipartFile[] files, String[] captions) throws IOException {
        var savedPost = createCaption(postDTO);
        for(int i = 0 ;i <files.length; i++){
            var cap = "";
            if(captions != null && i < captions.length){
                cap = captions[i]+"";
            }
            var resource = new Resource();
            savedPost.setPostType(Post.PostType.RESOURCE);
            resource.setPost(savedPost);
            resource.setDescription(cap);
            resource.setDate(new Date());
            if(isValidVideoExtension(getFileExtension(files[i]))){
                resource.setVideo(uploadVideo(files[i]));
            }
            if(isValidPhotoExtension(getFileExtension(files[i]))) {
                resource.setPhoto(uploadPhoto(files[i]));
            }
            resourceRepository.save(resource);
        }
        return  savedPost;
    }

    private Post createCaption(PostDto postDTO){
        var post = new Post();
        post.setDescription(postDTO.getContent());
        post.setPostType(Post.PostType.CONTENT);
        post.setCreatedDate(new Date());
        post.setUser(getCurrentLoginUser());
        if(postDTO.getGroupId()!=null){
            post.setAccess(Access.PRIVATE);
            var user_group = new User_Group();
            user_group.setUser(getCurrentLoginUser());
            user_group.setCommunity(communityRepository.findById(Long.valueOf(postDTO.getGroupId())).orElseThrow(()->new CommunityHubException("not found community")));
            user_groupRepository.save(user_group);
            post.setUser_group(user_group);
        }else{
            post.setAccess(Access.PUBLIC);
        }
        return postRepository.save(post);
    }

    private User getCurrentLoginUser(){
        return userService.findByStaffId(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new CommunityHubException("user not found"));
    }

    public String getFileExtension(MultipartFile file){
        return file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')).toLowerCase();
    }

    public String uploadVideo(MultipartFile file) throws IOException {
        return cloudinary.uploader()
                .upload(file.getBytes(), Map.of("resource_type", "video","public_id", UUID.randomUUID().toString()))
                .get("url").toString();
    }

    public String uploadPhoto(MultipartFile file) throws IOException {
        return cloudinary.uploader()
                .upload(file.getBytes(), Map.of( "public_id", UUID.randomUUID().toString()))
                .get("url").toString();
    }

}
