package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.models.Community;
import com.communityHubSystem.communityHub.models.User;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface CommunityService {
    public Community createCommunity(MultipartFile file,Community community, Long id) throws IOException;

    public List<User> getAll();

  //  public List<Community> getAllCommunity(Model model);

    public Community getCommunityBy(Long id);

   public void createGroup(MultipartFile file,Community community,List<Long> id);

    public List<String> getOwnerNamesByCommunityId(Long communityId);
    public void kickGroup(Community community,List<Long> id);

    public Community getCommunityById(Long communityId);
    public List<Community> findAll();

    public List<Community> findAllByIsActive();

    public List<Community> getAllCommunityWithUserId();
    public List<Community> communitySearchMethod(String input);

    public boolean existsByName(String name);
}
