package com.communityHubSystem.communityHub.impls;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.communityHubSystem.communityHub.dto.FirstUpdateDto;
import com.communityHubSystem.communityHub.dto.PostDto;
import com.communityHubSystem.communityHub.dto.SecondUpdateDto;
import com.communityHubSystem.communityHub.dto.ViewPostDto;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.*;
import com.communityHubSystem.communityHub.repositories.*;
import com.communityHubSystem.communityHub.services.PostService;
import com.communityHubSystem.communityHub.services.UserService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    private final ModelMapper modelMapper;
    private final SavedPostRepository savedPostRepository;

    private final List<String> photoExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", "bmp", "tiff", "tif", "psv", "svg", "webp", "ico", "heic");
    private final List<String> videoExtensions = Arrays.asList(".mp4", ".avi", ".mov", ".wmv", "mkv", "flv", "mpeg", "mpg", "webm", "3gp", "ts");


    @Transactional
    @Override
    public Post createPost(PostDto postDTO, MultipartFile[] files, String[] captions) throws IOException {
        System.out.println("Service" + postDTO);
        if (files == null) {
            return createCaption(postDTO);
        } else {
            return createResource(postDTO, files, captions);
        }

    }

    @Override
    public Post createRawFilePost(PostDto postDto, MultipartFile[] files ) throws IOException, ExecutionException, InterruptedException {
        var post = createCaption(postDto);
        post.setPostType(Post.PostType.RAW);
        for(int i = 0 ; i< files.length ; i++){
            var raw = new Resource();
            raw.setDescription(files[i].getOriginalFilename());
            raw.setRaw(uploadRawFile(files[i]));
            raw.setDate(new Date());
            raw.setPost(post);
            resourceRepository.save(raw);
        }
        return postRepository.save(post);
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
        var found = postRepository.findById(id).orElseThrow(() -> new CommunityHubException("post not found"));
        found.setDeleted(true);
        postRepository.save(found);
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
    public Post firstUpdateRaw(FirstUpdateDto firstUpdateDto, MultipartFile[] multipartFiles) throws IOException {
        var found = postRepository.findById(Long.valueOf(firstUpdateDto.getPostId())).orElseThrow(()->new CommunityHubException("post not found"));
        found.setDescription(firstUpdateDto.getUpdatePostText());
      if(multipartFiles != null){
          for (var multipartFile : multipartFiles) {
              var raw = new Resource();
              raw.setDescription(multipartFile.getOriginalFilename());
              System.err.println(multipartFile.getOriginalFilename()+"]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]");
              raw.setRaw(uploadRawFile(multipartFile));
              raw.setDate(new Date());
              raw.setPost(found);
              resourceRepository.save(raw);
          }
      }
        return postRepository.save(found);
    }

    @Override
    @Transactional
    public Post secondUpdate(List<SecondUpdateDto> secondUpdateDto) {
        var found = new Post();
//        Long postId = null;
        for (var s : secondUpdateDto) {
            if (Objects.equals(s.getPostCaption(), "deleted")) {
                var del = resourceRepository.findById(Long.valueOf(s.getResourceId())).orElseThrow(() -> new CommunityHubException("resource not found"));
                resourceRepository.deleteWithId(Long.parseLong(s.getResourceId()));
                Long   postId = del.getPost().getId();
                var post = postRepository.findById(postId).orElseThrow(() -> new CommunityHubException("not found"));
                postRepository.save(post);
            } else {
                var resource = resourceRepository.findById(Long.valueOf(s.getResourceId())).orElseThrow(() -> new CommunityHubException(("resource not found")));
                resource.setDescription(s.getPostCaption());
                resourceRepository.save(resource);
                found = resource.getPost();
            }
        }
   //   var originalPost = postRepository.findById(postId).orElseThrow(() -> new CommunityHubException("Post not found Exception"));
        return found;
    }

    @Override
    public Post secondUpdateRaw(List<SecondUpdateDto> secondUpdateDtos) {
        var found = new Post();
//        Long postId = null;
        for (var s : secondUpdateDtos) {
            if (Objects.equals(s.getPostCaption(), "deleted")) {
                var del = resourceRepository.findById(Long.valueOf(s.getResourceId())).orElseThrow(() -> new CommunityHubException("resource not found"));
                resourceRepository.deleteWithId(Long.parseLong(s.getResourceId()));
                Long   postId = del.getPost().getId();
                var post = postRepository.findById(postId).orElseThrow(() -> new CommunityHubException("not found"));
                postRepository.save(post);
            } else {
                var resource = resourceRepository.findById(Long.valueOf(s.getResourceId())).orElseThrow(() -> new CommunityHubException(("resource not found")));
                resource.setDescription(s.getPostCaption());
                resourceRepository.save(resource);
                found = resource.getPost();
            }
        }
        //   var originalPost = postRepository.findById(postId).orElseThrow(() -> new CommunityHubException("Post not found Exception"));
        return found;
    }


    @Override
    public List<Object> getFivePostsPerTime(String page) {
        var pageable = PageRequest.of(Math.toIntExact(Long.parseLong(page)), 5, Sort.by("createdDate").ascending());
        var posts = postRepository.findAll(pageable);
        var objs = new ArrayList<Object>();
        objs.add(page);
        objs.add(posts.getContent());
        return objs;
    }

    //    @Override
//    public Page<Post> findPostRelatedToUser(String page) {
//        var loginStaffId = SecurityContextHolder.getContext().getAuthentication().getName();
//        var loginUser = userService.findByStaffId(loginStaffId)
//                .orElseThrow(() -> new CommunityHubException("User not found!"));
//        List<Post> postList = new ArrayList<>();
//        List<Post> publicPosts = postRepository.findPostsByAccess(Access.PUBLIC);
//        postList.addAll(publicPosts);
//        List<User_Group> userGroupList = user_groupRepository.findByUserId(loginUser.getId());
//        for(User_Group user_group:userGroupList){
//            List<Post> userGroupPosts = postRepository.findAllByUserIdAndUserGroupId(loginUser.getId(),user_group.getId());
//            postList.addAll(userGroupPosts);
//        }
//        Pageable pageable = PageRequest.of(Integer.parseInt(page), 5);
//        int start = Math.toIntExact(pageable.getOffset());
//        if (start >= postList.size()) {
//            return Page.empty(pageable);
//        }
//        int end = Math.min(start + pageable.getPageSize(), postList.size());
//        List<Post> paginatedPosts = postList.subList(start, end);
//        Page<Post> postPage = new PageImpl<>(paginatedPosts, pageable, postList.size());
//        return postPage;
//    }
    @Override
    public Page<Post> findPostRelatedToUser(String page) {

        var loginStaffId = SecurityContextHolder.getContext().getAuthentication().getName();
        var loginUser = userService.findByStaffId(loginStaffId)
                .orElseThrow(() -> new CommunityHubException("User not found!"));

        if(loginUser.getRole().equals(User.Role.ADMIN)){
            var adminList = postRepository.findAll().stream().filter(a -> !a.isDeleted() && !a.isHide()).sorted(Comparator.comparing(Post::getCreatedDate).reversed()).toList();
            Pageable pageable = PageRequest.of(Integer.parseInt(page), 5);
            int start = Math.toIntExact(pageable.getOffset());
            if (start >= adminList.size()) {
                return Page.empty(pageable);
            }
            int end = Math.min(start + pageable.getPageSize(), adminList.size());
            List<Post> paginatedPosts = adminList.subList(start, end);
            return new PageImpl<>(paginatedPosts, pageable, adminList.size());

        }

        List<Post> publicPosts = postRepository.findPostsByAccessOrderByCreatedDateDesc(Access.PUBLIC);
        var notDeletedPosts = publicPosts.stream().filter(p -> !p.isDeleted() && !p.isHide()).toList();
        List<Post> postList = new ArrayList<>(notDeletedPosts);
        List<User_Group> userGroupList = user_groupRepository.findByUserId(loginUser.getId());
        System.out.println("SIZEOFUSERGROUP"+userGroupList.size());
        List<User_Group> user_groups = new ArrayList<>();
        for(User_Group user_group:userGroupList){
            List<User_Group> groups = user_groupRepository.findByCommunityId(user_group.getCommunity().getId());
            user_groups.addAll(groups);
        }
        for (User_Group user_group : user_groups) {
            List<Post> userGroupPosts = postRepository.findAllByUserGroupIdOrderByCreatedDateDesc(user_group.getId());
            var filteredList = userGroupPosts.stream().filter(u -> !u.isDeleted() && !u.isHide()).toList();
            System.err.println("WOWOOWOWOWO"+user_group.getId());
            System.err.println("WOWOOWOWOWO"+filteredList.size());
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

    @Override
    public List<Object> checkPostOwnerOrAdmin(Long id) {
        var obj = new ArrayList<Object>();
        var found = postRepository.findById(id).orElseThrow(() -> new CommunityHubException("post not found"));
        var loginUser = getCurrentLoginUser();
        if (loginUser.getId().equals(found.getUser().getId())) {
            obj.add("OWNER");
            return obj;
        } else if (loginUser.getRole().equals(User.Role.ADMIN)) {
            obj.add("ADMIN");
            return obj;
        } else {
            obj.add("NO");
            return obj;
        }
    }

    @Override
    public Page<Post> returnPostForUserDetailPage(Long id,String page) {
        var posts = new ArrayList<>(postRepository.findPostsByUserId(id).stream().filter(p -> !p.isDeleted() && !p.isHide()).toList());
        System.err.println(posts);
        posts.sort(Comparator.comparing(Post::getCreatedDate).reversed());
        Pageable pageable = PageRequest.of(Integer.parseInt(page), 5);
        int start = Math.toIntExact(pageable.getOffset());
        if (start >= posts.size()) {
            return Page.empty(pageable);
        }
        int end = Math.min(start + pageable.getPageSize(), posts.size());
        List<Post> paginatedPosts = posts.subList(start, end);
        return new PageImpl<>(paginatedPosts, pageable, posts.size());
    }

    @Override
    public List<Post> findAllPostByIsDeleted(boolean value,Long id) {
        return postRepository.findAllPostsByIsDeletedAndUserId(value,id);
    }

    @Override
    public List<Post> findAllPostByIsDeletedAndUserGroupId(Long id) {
        return postRepository.findAllByIsDeletedAndUserGroupIdJPQL(false,id);
    }

    @Override
    public Post findByUrl(String url) {
        return postRepository.findByUrl(url);
    }

    @Override
    public List<Post> findByYearAndMonth(int year, int month) {
        return postRepository.findByYearAndMonth(year,month);
    }

    @Override
    public List<Post> findByUserIdAndYearAndMonth(Long userId, int year, int month) {
        return postRepository.findByUserIdAndYearAndMonth(userId,year,month);
    }

    @Override
    public List<Post> getPostsByCurrentMonthAndYearAndUserId(Long userId) {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        return postRepository.findByUserIdAndYearAndMonth(userId,currentYear,currentMonth);
    }

    @Override
    public Page<Post> getHidePostOfAUser(Long id,String  page) {
        var posts = postRepository.findDeletedPostsByUserId(id);
        System.err.println(posts);
        posts.sort(Comparator.comparing(Post::getCreatedDate).reversed());
        Pageable pageable = PageRequest.of(Integer.parseInt(page), 5);
        int start = Math.toIntExact(pageable.getOffset());
        if (start >= posts.size()) {
            return Page.empty(pageable);
        }
        int end = Math.min(start + pageable.getPageSize(), posts.size());
        List<Post> paginatedPosts = posts.subList(start, end);
        return new PageImpl<>(paginatedPosts, pageable, posts.size());
    }

    @Override
    @Transactional
    public Post hideAPost(Long id) {
        var post = postRepository.findById(id).orElseThrow(()->new CommunityHubException("post not found"));
        post.setHide(true);
        postRepository.save(post);
        return post;
    }

    @Override
    public Post showAPost(Long id) {
        var post = postRepository.findById(id).orElseThrow(()->new CommunityHubException("post not found"));
        post.setHide(false);
        postRepository.save(post);
        return post;
    }



    @Override
    public Page<Post> getAllSavedPost(String  page) {
        var savedPosts = savedPostRepository.findByUserId(getCurrentLoginUser().getId());
        var result = new ArrayList<>(savedPosts.
                stream()
                .map(s -> postRepository.findById(s.getPostId()).orElseThrow(() -> new CommunityHubException("post not found")))
                .toList());
        result.sort(Comparator.comparing(Post::getCreatedDate).reversed());
        Pageable pageable = PageRequest.of(Integer.parseInt(page), 5);
        int start = Math.toIntExact(pageable.getOffset());
        if (start >= result.size()) {
            return Page.empty(pageable);
        }
        int end = Math.min(start + pageable.getPageSize(), result.size());
        List<Post> paginatedPosts = result.subList(start, end);
        return new PageImpl<>(paginatedPosts, pageable, result.size());
    }

    @Override
    public List<Object> checkSavedPost(Long id) {
        var list = new ArrayList<Object>();
        var found = savedPostRepository.findByUserIdAndPostId(getCurrentLoginUser().getId(),id);
        if(found != null){
            list.add("SAVED");
        }else {
            list.add("UNSAVED");
        }
        return list;
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
        var staffId = SecurityContextHolder.getContext().getAuthentication().getName();
        var loginUser = userService.findByStaffId(staffId).orElseThrow(() -> new CommunityHubException("User Name not found Exception"));
        var post = new Post();
        post.setHide(false);
        post.setDescription(postDTO.getContent());
        post.setPostType(Post.PostType.CONTENT);
        post.setCreatedDate(new Date());
        post.setUser(getCurrentLoginUser());
        post.setDeleted(false);
        if ( postDTO.getGroupId()!= null && Long.parseLong(postDTO.getGroupId()) > 0) {
            post.setAccess(Access.PRIVATE);
            if(user_groupRepository.findByUserIdAndCommunityId(loginUser.getId(),Long.valueOf(postDTO.getGroupId()))==null){
                var user_group =new User_Group();
                user_group.setCommunity(communityRepository.findById(Long.valueOf(postDTO.getGroupId())).orElseThrow(()->new CommunityHubException("not found community")));
                user_group.setUser(loginUser);
                user_group.setDate(new Date());
                user_groupRepository.save(user_group);
                post.setUserGroup(user_group);
            }else {
            var community = communityRepository.findById(Long.valueOf(postDTO.getGroupId())).orElseThrow(() -> new CommunityHubException("Group Name Not found Exception!"));
            var user_group = user_groupRepository.findByUserIdAndCommunityId(loginUser.getId(),community.getId());
            post.setUserGroup(user_group);
            }
        } else {
            post.setAccess(Access.PUBLIC);
        }
        post = postRepository.save(post);
        post.setUrl(generateUniqueUrl(post.getId()));
        return postRepository.save(post);
    }

    public User getCurrentLoginUser() {
        return userService.findByStaffId(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new CommunityHubException("user not found"));
    }

    public String getFileExtension(MultipartFile file) {
        var filename =file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.'));
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

    public  String uploadRawFile(MultipartFile file) throws IOException {
        var extension = getFileExtension(file);
        var publicId = UUID.randomUUID().toString() + extension;
        var uploadResult = cloudinary.uploader().uploadLargeRaw(file.getBytes(),
                ObjectUtils.asMap("resource_type", "raw", "public_id", publicId,"filename",file.getOriginalFilename()));
        return  uploadResult.get("url").toString();
    }

    private String ensureHttps(String url) {
        if (url.startsWith("http://")) {
            return url.replaceFirst("http://", "https://");
        }
        return url;
    }


    private byte[] compressFile(MultipartFile file) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

        ZipEntry zipEntry = new ZipEntry(Objects.requireNonNull(file.getOriginalFilename()));
        zipEntry.setSize(file.getSize());
        zipOutputStream.putNextEntry(zipEntry);
        zipOutputStream.write(file.getBytes());
        zipOutputStream.closeEntry();
        zipOutputStream.close();
        byteArrayOutputStream.close();

        return byteArrayOutputStream.toByteArray();
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


    public static String generateUniqueUrl(Long postId) {
        return "https://communityHub.com/posts/" + postId  + UUID.randomUUID().toString();
    }

}
