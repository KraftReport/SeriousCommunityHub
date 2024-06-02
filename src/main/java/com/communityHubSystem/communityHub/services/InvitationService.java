package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.dto.InvitationDto;
import com.communityHubSystem.communityHub.models.Community;
import com.communityHubSystem.communityHub.models.Invitation;
import com.communityHubSystem.communityHub.models.User;

import java.util.List;

public interface InvitationService {

    void save(Long id, InvitationDto invitationDto);

    public List<Invitation> findLoginUserInvitation(Long id);

   public void findById(Long id);

   public void acceptedInvitation(Long id, Long communityId);

   public List<Invitation> findByCommunityIdAndIsInvited(Long id, boolean b);

    public List<Invitation> findByCommunityIdAndIsRemoved(Long id, boolean b);

    void requestedInvitation(User user, User loginUser, Community community);

    void requestAcceptedInvitation(Long id);

    void requestDeniedInvitation(Long id);

   public List<Invitation> findByCommunityIdAndIsRemovedAndIsRequested(Long id, boolean b, boolean b1);

    public List<Invitation> findInvitationsByCommunityIdAndIsRemovedAndIsRequested(Long id, boolean b, boolean b1);

    public List<Invitation> findByRecipientId(Long uId);

    public List<Invitation> findBySenderId(Long uId);

    void deleteById(Long id);
}
