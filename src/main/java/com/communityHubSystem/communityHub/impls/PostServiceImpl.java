package com.communityHubSystem.communityHub.impls;

import com.cloudinary.Cloudinary;
import com.communityHubSystem.communityHub.dto.FirstUpdateDto;
import com.communityHubSystem.communityHub.dto.PostDto;
import com.communityHubSystem.communityHub.dto.SecondUpdateDto;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.*;
import com.communityHubSystem.communityHub.repositories.*;
import com.communityHubSystem.communityHub.services.PostService;
import com.communityHubSystem.communityHub.services.UserService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
    private final List<String> photoExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", "bmp", "tiff", "tif", "psv", "svg", "webp", "ico", "heic");
    private final List<String> videoExtensions = Arrays.asList(".mp4", ".avi", ".mov", ".wmv", "mkv", "flv", "mpeg", "mpg", "webm", "3gp", "ts");


    @Transactional
    @Override
    public Post createPost(PostDto postDTO, MultipartFile[] files, String[] captions) throws IOException {
        System.out.println("Service"+postDTO);
        if (files == null) {
            return createCaption(postDTO);
        } else {
            return createResource(postDTO, files, captions);
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

    @Override
    public List<Post> findPostByUserId(Long id) {
        return postRepository.findPostsByUserId(id);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        resourceRepository.deleteByPostId(id);
        postRepository.deleteById(id);
    }

    @Override
    public List<Post> searchMethod(String in) {
        var input = URLDecoder.decode(in, StandardCharsets.UTF_8);
        var specifications = new ArrayList<Specification<Post>>();
        if (StringUtils.hasLength(input)) {
            specifications.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + input.toLowerCase() + "%"));
            specifications.add(joinOfPostAndResource(input));
            specifications.add(joinOfPostAndUser(input));
        }

        Specification<Post> postSpec = Specification.where(null);
        for (var s : specifications) {
            postSpec = postSpec.or(s);
        }
        return postRepository.findAll(postSpec);
    }

    @Override
    @Transactional
    public Post firstUpdate(FirstUpdateDto firstUpdateDto, MultipartFile[] files, String[] captions) throws IOException {
        var found = postRepository.findById(Long.valueOf(firstUpdateDto.getPostId())).orElseThrow(() -> new CommunityHubException("post not found"));
        if (files != null) {
            found.setDescription(firstUpdateDto.getUpdatePostText());
            var savePost = postRepository.save(found);
            for (int i = 0; i < files.length; i++) {
                var cap = "";
                if (captions != null && i < captions.length) {
                    cap = captions[i] + "";
                }
                var resoruce = new Resource();
                resoruce.setPost(savePost);
                resoruce.setDescription(cap);
                resoruce.setDate(new Date());
                if (isValidPhotoExtension(getFileExtension(files[i]))) {
                    resoruce.setPhoto(uploadPhoto(files[i]));
                }
                if (isValidVideoExtension(getFileExtension(files[i]))) {
                    resoruce.setVideo(uploadVideo(files[i]));
                }
                resourceRepository.save(resoruce);
            }
            return savePost;
        }
        found.setDescription(firstUpdateDto.getUpdatePostText());
        postRepository.save(found);
        return found;
    }

    @Override
    @Transactional
    public Post secondUpdate(List<SecondUpdateDto> secondUpdateDto) {
        var found = new Post();
        for (var s : secondUpdateDto) {
            if (Objects.equals(s.getPostCaption(), "deleted")) {
                var del = resourceRepository.findById(Long.valueOf(s.getResourceId())).orElseThrow(() -> new CommunityHubException("resource not found"));
                resourceRepository.deleteWithId(Long.parseLong(s.getResourceId()));
                var postId = del.getPost().getId();
                var post = postRepository.findById(postId).orElseThrow(() -> new CommunityHubException("not found"));
                postRepository.save(post);
            } else {
                var resource = resourceRepository.findById(Long.valueOf(s.getResourceId())).orElseThrow(() -> new CommunityHubException(("resource not found")));
                resource.setDescription(s.getPostCaption());
                resourceRepository.save(resource);
                found = resource.getPost();
            }
        }
        return found;
    }

//    @Override
//    public List<Post> getFivePostsPerTime(String page) {
////        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
////        Pageable pageable = PageRequest.of(Math.toIntExact(Long.parseLong(page)), 7, sort);
//        var pageable = PageRequest
//                .of(Math.toIntExact(Long.parseLong(page)),
//                        5, Sort.by("createdDate").ascending());
//        var posts = postRepository.findAll(pageable);
//        return posts.getContent();
//    }

    @Override
    public List<Object> getFivePostsPerTime(String  page) {
        var pageable = PageRequest.of(Math.toIntExact(Long.parseLong(page)),5,Sort.by("createdDate").ascending());
        var posts = postRepository.findAll(pageable);
        var objs = new ArrayList<Object>();
        objs.add(page);
        objs.add(posts.getContent());
        return objs;
    }


    public boolean isValidPhotoExtension(String extension) {
        return photoExtensions.contains(extension);
    }

    private boolean isValidVideoExtension(String extension) {
        return videoExtensions.contains(extension);
    }


    @Transactional
    public Post createResource(PostDto postDTO, MultipartFile[] files, String[] captions) throws IOException {
        var savedPost = createCaption(postDTO);
        for (int i = 0; i < files.length; i++) {
            var cap = "";
            if (captions != null && i < captions.length) {
                cap = captions[i] + "";
            }
            var resource = new Resource();
            savedPost.setPostType(Post.PostType.RESOURCE);
            resource.setPost(savedPost);
            resource.setDescription(cap);
            resource.setDate(new Date());
            if (isValidVideoExtension(getFileExtension(files[i]))) {
                resource.setVideo(uploadVideo(files[i]));
            }
            if (isValidPhotoExtension(getFileExtension(files[i]))) {
                resource.setPhoto(uploadPhoto(files[i]));
            }
            resourceRepository.save(resource);
        }
        return savedPost;
    }

    @Transactional
    public Post createCaption(PostDto postDTO) {
        var post = new Post();
        post.setDescription(postDTO.getContent());
        post.setPostType(Post.PostType.CONTENT);
        post.setCreatedDate(new Date());
        post.setUser(getCurrentLoginUser());
        if (Long.parseLong(postDTO.getGroupId()) > 0) {
            post.setAccess(Access.PRIVATE);
            var user_group = new User_Group();
            user_group.setUser(getCurrentLoginUser());
            user_group.setCommunity(communityRepository.findById(Long.valueOf(postDTO.getGroupId())).orElseThrow(() -> new CommunityHubException("not found community")));
            user_groupRepository.save(user_group);
            post.setUser_group(user_group);
        } else {
            post.setAccess(Access.PUBLIC);
        }
        return postRepository.save(post);
    }

    public User getCurrentLoginUser() {
        return userService.findByStaffId(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new CommunityHubException("user not found"));
    }

    public String getFileExtension(MultipartFile file) {
        return file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')).toLowerCase();
    }

    public String uploadVideo(MultipartFile file) throws IOException {
        return cloudinary.uploader()
                .upload(file.getBytes(), Map.of("resource_type", "video", "public_id", UUID.randomUUID().toString()))
                .get("url").toString();
    }

    public String uploadPhoto(MultipartFile file) throws IOException {
        return cloudinary.uploader()
                .upload(file.getBytes(), Map.of("public_id", UUID.randomUUID().toString()))
                .get("url").toString();
    }

    public static Specification<Post> joinOfPostAndResource(String caption) {
        return (root, query, criteriaBuilder) -> {
            if (caption != null && !caption.isEmpty()) {
                Join<Post, Resource> postResourceJoin = root.join("resources", JoinType.LEFT);
                return criteriaBuilder.like(criteriaBuilder.lower(postResourceJoin.get("description")), "%" + caption.toLowerCase() + "%");
            }
            return criteriaBuilder.disjunction();
        };
    }

    public static Specification<Post> joinOfPostAndUser(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name != null && !name.isEmpty()) {
                Join<Post, User> postUserJoin = root.join("user");
                return criteriaBuilder.like(criteriaBuilder.lower(postUserJoin.get("name")), "%".concat(name.toLowerCase()).concat("%"));
            }
            return criteriaBuilder.disjunction();
        };
    }

}
