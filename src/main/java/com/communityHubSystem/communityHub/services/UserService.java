package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.dto.UserDTO;
import com.communityHubSystem.communityHub.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    User updateProfilePhoto(MultipartFile multipartFile) throws IOException;
    public User saveImage(MultipartFile multipartFile) throws IOException;
    void updateUserStatus(Long userId, boolean isActive, String banReason);
    boolean existsByStaffId(String staffId);
    public void updateUserToAdminStatus(Long userId, boolean pending, boolean done,boolean removed);
    public void updateAdminToUserStatus(Long userId,  boolean isRemoved ,String removedReason);
    public void rejectAdminRole(Long userId,boolean pending,boolean done,boolean removed,boolean reject,String rejectReason);
    public void updateUserRoleToAdmin(Long userId);
    public  void acceptReject(Long userId);
    public List<User> userSearchMethod(String input) throws UnsupportedEncodingException;

}
