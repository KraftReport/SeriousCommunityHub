package com.communityHubSystem.communityHub.impls;

import com.cloudinary.Cloudinary;
import com.communityHubSystem.communityHub.dto.UserDTO;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.*;
import com.communityHubSystem.communityHub.repositories.*;
import com.communityHubSystem.communityHub.services.ExcelUploadService;
import com.communityHubSystem.communityHub.services.UserService;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final ExcelUploadService excelUploadService;
    private final PostRepository postRepository;
    private final ResourceRepository resourceRepository;
    private final User_GroupRepository user_groupRepository;
    private final List<String> photoExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", "bmp","tiff","tif","psv","svg","webp","ico","heic");
    private final Cloudinary cloudinary;


    @Transactional
    @Override
    public void updateUserData(User user) {
    userRepository.findById(user.getId()).ifPresent(user1 -> {
        user1.setUser_skills(user.getUser_skills());
        user1.setUser_groups(user.getUser_groups());
        user1.setActive(user.isActive());
        user1.setGender(user.getGender());
        user1.setPhone(user.getPhone());
        user1.setPhoto(user.getPhoto());
        user1.setHobby(user.getHobby());
        user1.setRole(user.getRole());
        user1.setPosts(user.getPosts());
        userRepository.save(user1);
    });}

    @Override
    public List<User> getAllUser() {
       List<User> listUser = new ArrayList<>();
       List<User> users = userRepository.findAll();
       for(User user:users){
           if(user.isActive()){
               listUser.add(user);
           }
       }
       return listUser;
    }



    @Override
    public List<User> searchMethod(UserDTO userDTO) {
        var staffId = userDTO.getStaffId();
        var name = userDTO.getName();
        var email = userDTO.getEmail();
        var team = userDTO.getTeam();
        var division = userDTO.getDivision();
        var department = userDTO.getDepartment();
        var isActive = userDTO.isActive();
        var skillNameList = userDTO.getSkillNameList();
        var specifications = new ArrayList<Specification<User>>();
        if(StringUtils.hasLength(staffId)){
            specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("staffId"),staffId));
        }
        if(StringUtils.hasLength(name)){
            specifications.add((root, query, criteriaBuilder) ->
                    criteriaBuilder
                            .like(criteriaBuilder.lower(root.get("name")),"%".concat(name.toLowerCase()).concat("%")));
        }
        if(StringUtils.hasLength(email)){
            specifications.add((root, query, criteriaBuilder) ->
                    criteriaBuilder
                            .like(criteriaBuilder.lower(root.get("email")),"%".concat(email.toLowerCase()).concat("%")));
        }

        if(StringUtils.hasLength(team)){
            specifications.add((root, query, criteriaBuilder) ->
                    criteriaBuilder
                            .like(criteriaBuilder.lower(root.get("team")),"%".concat(team.toLowerCase()).concat("%")));
        }
        if(StringUtils.hasLength(division)){
            specifications.add((root, query, criteriaBuilder) ->
                    criteriaBuilder
                            .like(criteriaBuilder.lower(root.get("division")),"%".concat(division.toLowerCase()).concat("%")));
        }
        if(StringUtils.hasLength(department)){
            specifications.add((root, query, criteriaBuilder) ->
                    criteriaBuilder
                            .like(criteriaBuilder.lower(root.get("dept")),"%".concat(department.toLowerCase()).concat("%")));
        }

        if(skillNameList!=null){
            specifications.add(getUserFromSkill(skillNameList));
        }
        Specification<User> userSpec = Specification.where(null);
        for(var s : specifications){
            userSpec = userSpec.and(s);
        }
        return userRepository.findAll(userSpec);
    }

    @Transactional
    @Override
    public Optional<User> findByStaffId(String staffId) {
        return userRepository.findByStaffId(staffId);
    }

    @Override
    public User findById(Long id) {
      return userRepository.findById(id).orElseThrow();
    }

    @Override
    public List<User> getAllUserWithoutAdmin() {
        Specification<User> spec = (root, query, builder) -> builder.equal(root.get("role"), User.Role.USER);
        return userRepository.findAll(spec);
    }

    @Override
    public void updateUserStatus(Long userId, boolean isActive, String banReason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        user.setActive(isActive);
        user.setBannedReason(banReason);
        userRepository.save(user);
    }

    @Override
    public boolean existsByStaffId(String staffId) {
        return userRepository.existsByStaffId(staffId);
    }

    @Override
    public User updateProfilePhoto(MultipartFile multipartFile) throws IOException {
        var found =  userRepository.findByStaffId(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new CommunityHubException("user not found"));
        if(isValidPhotoExtension(getFileExtension(multipartFile))){
            found.setPhoto(uploadPhoto(multipartFile));
        }else {
            found.setPhoto(found.getPhoto());
        }
        return userRepository.save(found);
    }

    @Override
    public User saveImage(MultipartFile multipartFile) throws IOException {
        var user = getCurrentLoginUser();
        var url = uploadPhoto(multipartFile);
        System.err.println(url);
        user.setPhoto(url);
        userRepository.save(user);
        return user;
    }

    public User getCurrentLoginUser() {
        return userRepository.findByStaffId(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new CommunityHubException("user not found"));
    }

    public String uploadPhoto(MultipartFile file) throws IOException {
        return cloudinary.uploader()
                .upload(file.getBytes(), Map.of( "public_id", UUID.randomUUID().toString()))
                .get("url").toString();
    }

    public boolean isValidPhotoExtension(String extension) {
        return photoExtensions.contains(extension);
    }

    public String getFileExtension(MultipartFile file){
        return file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')).toLowerCase();
    }

    public static Specification<User> getUserFromSkill(List<String > skillNameList){
        return (root, query, criteriaBuilder) -> {
            if(!skillNameList.isEmpty()){
                Join<User, User_Skill> userSkillJoin = root.join("user_skills");
                Join<User_Group,Skill> skillUserJoin = userSkillJoin.join("skill");
                var users = criteriaBuilder.disjunction();
                for(var s : skillNameList){
                    users = criteriaBuilder.or(users,criteriaBuilder.like(criteriaBuilder.lower(skillUserJoin.get("name")),"%".concat(s.toLowerCase()).concat("%")));
                }
                return users;
            }
            return criteriaBuilder.conjunction();
        };
    }

    @Override
    public void saveUserToDatabase(MultipartFile file){
        if(excelUploadService.isValidExcelFile(file)){
            try {
                List<User> employeeData = excelUploadService.getEmployeeDataFromExcel(file.getInputStream());
                this.userRepository.saveAll(employeeData);
            } catch (IOException e) {
                throw new IllegalArgumentException("The file is not a valid excel file");
            }
        }
    }



    @Override
    public void updateUserRoleToAdmin(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            user.get().setRole(User.Role.ADMIN);
            userRepository.save(user.get());
        }
    }


    @Override
    @Transactional
    public void updateUserToAdminStatus(Long userId, boolean pending, boolean done,boolean removed) {
        System.out.println("it still reach");
        System.out.println(pending);
        System.out.println(done);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        System.out.println("user is present");
        user.setPending(pending);
        user.setDone(done);
        user.setRemoved(removed);
        user.setRemovedReason(null);
        userRepository.save(user);
    }
    @Override
    @Transactional
    public  void rejectAdminRole(Long userId,boolean pending,boolean done,boolean removed,boolean reject,String rejectReason){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        System.out.println("user is present");
        user.setPending(pending);
        user.setDone(done);
        user.setRemoved(removed);
        user.setRejected(reject);
        user.setRejectReason(rejectReason);
        user.setRejectedCount(user.getRejectedCount()+1);
        userRepository.save(user);
    }
    @Override
    @Transactional
    public  void acceptReject(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        System.out.println("user is present");
        user.setRejected(false);
        user.setRejectReason(null);
        userRepository.save(user);
    }

    @Override
    public List<User> userSearchMethod(String in) throws UnsupportedEncodingException {
        System.err.println(in+"iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
        var input = URLDecoder.decode(in, StandardCharsets.UTF_8);
        System.err.println(input+"-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
        var specifications = new ArrayList<Specification<User>>();
        if(StringUtils.hasLength(input)){
            specifications.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),"%".concat(input.toLowerCase()).concat("%")));
            specifications.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),"%".concat(input.toLowerCase()).concat("%")));
            specifications.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("division")),"%".concat(input.toLowerCase()).concat("%")));
            specifications.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("dept")),"%".concat(input.toLowerCase()).concat("%")));
            specifications.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("team")),"%".concat(input.toLowerCase()).concat("%")));
            specifications.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("hobby")),"%".concat(input.toLowerCase()).concat("%")));
        }


        Specification<User> userSpec = Specification.where(null);
        for(var s : specifications){
            userSpec = userSpec.or(s);
        }
        return userRepository.findAll(userSpec);
    }

    @Override
    public List<Object> checkUserOrAdminOrGroupOwner() {
        var loginUser = getCurrentLoginUser();
        var objList = new ArrayList<Object>();
        if(loginUser.getRole().equals(User.Role.ADMIN)){
            objList.add("ADMIN");
            return objList;
        } else if (loginUser.getRole().equals(User.Role.USER) && checkGroupOwnerOrNot()){
            objList.add("OWNER");
            return objList;
        }else {
            objList.add("MEMBER");
            return objList;
        }
    }

    @Override
    public User getLogin() {
        return getCurrentLoginUser();
    }

    @Override
    public Long getCountOfUserUploadPhoto(Long id) {
        var user = userRepository.findById(id).orElseThrow(()->new CommunityHubException("user not found"));
        var posts = postRepository.findPostsByUserId(id);
        var resources = new ArrayList<Resource>();
        posts.forEach(p->{
            resources.addAll(resourceRepository.getResourcesByPostId(p.getId()).stream().filter(r->r.getVideo()==null).toList());
        });
        return (long) resources.size();
    }

    @Override
    public Long getCountOfUserUploadVideo(Long id) {
        var user = userRepository.findById(id).orElseThrow(()->new CommunityHubException("user not found"));
        var posts = postRepository.findPostsByUserId(id);
        var resources = new ArrayList<Resource>();
        posts.forEach(p->{
            resources.addAll(resourceRepository.getResourcesByPostId(p.getId()).stream().filter(r->r.getPhoto()==null).toList());
        });
        return (long) resources.size();
    }

    @Override
    public List<User> getAllActiveUser() {
        List<User> users = new ArrayList<>();
        List<User> userList = userRepository.findAll();
        for(User user:userList) {
          if(user.isActive()){
              users.add(user);
          }
        }
        return users;
    }

    @Override
    public User mentionedUser(String name) {
        return userRepository.findByName(name.trim());
    }

    @Override
    public List<Object> checkIfUserIsAMemberOrOwnerOrAdminOfAGroup(Long id) {
        var list = new ArrayList<Object>();

        var community = communityRepository.findById(id)
                .orElseThrow(() -> new CommunityHubException("Community not found"));
        var user = getCurrentLoginUser();

        if (user.getRole() == User.Role.ADMIN) {
            list.add("ADMIN");
            return list;
        }

        var found = user_groupRepository.findByUserIdAndCommunityId(user.getId(), community.getId());

        if (found == null) {
            list.add("VISITOR");
        } else if (community.getOwnerName().equals(user.getName())) {
            list.add("OWNER");
        } else {
            list.add("MEMBER");
        }

        return list;
    }

    @Transactional
    @Override
    public void notiChangeToTurnOff(User loginUser) {
        userRepository.findById(loginUser.getId()).ifPresent(u -> {
            u.setIsOn(User.IsOn.OFF);
            userRepository.save(u);
        });
    }

    @Transactional
    @Override
    public void notiChangeToTurnOn(User loginUser) {
        userRepository.findById(loginUser.getId()).ifPresent(u -> {
            u.setIsOn(User.IsOn.ON);
            userRepository.save(u);
        });
    }

    @Override
    public User findByName(String name) {
        return userRepository.findByName(name);
    }


    private boolean checkGroupOwnerOrNot(){
        var loginUser = getCurrentLoginUser();
        return communityRepository.findCommunityByOwnerName(loginUser.getName()) != null;
    }

    @Override
    @Transactional
    public void updateAdminToUserStatus(Long userId, boolean isRemoved, String removedReason){
        System.out.println("it reach 22");
        User user= userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found" +
                "with ID: "+userId));
        user.setRemoved(isRemoved);
        user.setDone(false);
        user.setRole(User.Role.USER);
        user.setRemovedReason(removedReason);
        userRepository.save(user);
    }



}
