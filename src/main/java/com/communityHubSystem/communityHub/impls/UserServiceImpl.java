package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.dto.UserDTO;
import com.communityHubSystem.communityHub.models.Skill;
import com.communityHubSystem.communityHub.models.User;
import com.communityHubSystem.communityHub.models.User_Group;
import com.communityHubSystem.communityHub.models.User_Skill;
import com.communityHubSystem.communityHub.repositories.UserRepository;
import com.communityHubSystem.communityHub.services.ExcelUploadService;
import com.communityHubSystem.communityHub.services.UserService;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final ExcelUploadService excelUploadService;

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
        return userRepository.findAll();
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
    public void updateUserStatus(Long userId, boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        user.setActive(isActive);
        userRepository.save(user);
    }

    @Override
    public boolean existsByStaffId(String staffId) {
        return userRepository.existsByStaffId(staffId);
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







}
