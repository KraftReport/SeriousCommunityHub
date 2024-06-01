package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.dto.InvitationDto;
import com.communityHubSystem.communityHub.models.Invitation;

import java.util.List;

public interface InvitationService {

    void save(Long id, InvitationDto invitationDto);

    public List<Invitation> findLoginUserInvitation(Long id);

   public void findById(Long id);

   public void acceptedInvitation(Long id, Long communityId);

   public List<Invitation> findByCommunityIdAndIsInvited(Long id, boolean b);

    public List<Invitation> findByCommunityIdAndIsRemoved(Long id, boolean b);
}
