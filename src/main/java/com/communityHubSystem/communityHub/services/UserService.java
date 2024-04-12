package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.dto.UserDTO;
import com.communityHubSystem.communityHub.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void saveUserToDatabase(MultipartFile file);
    public void updateUserData(User user);
    public List<User> getAllUser();
    public List<User> searchMethod(UserDTO userDTO);
    Optional<User> findByStaffId(String staffId);
    public User findById(Long id);
    public List<User> getAllUserWithoutAdmin();
    void updateUserStatus(Long userId, boolean isActive);
    boolean existsByStaffId(String staffId);
}
