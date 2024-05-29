package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.dto.InvitationDto;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.Invitation;
import com.communityHubSystem.communityHub.models.User;
import com.communityHubSystem.communityHub.models.User_Group;
import com.communityHubSystem.communityHub.repositories.InvitationRepository;
import com.communityHubSystem.communityHub.services.CommunityService;
import com.communityHubSystem.communityHub.services.InvitationService;
import com.communityHubSystem.communityHub.services.UserService;
import com.communityHubSystem.communityHub.services.User_GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final CommunityService communityService;
    private final UserService userService;
    private final User_GroupService user_groupService;

    @Transactional
    @Override
    public void save(Long id, InvitationDto invitationDto) {
        for (Long userId : invitationDto.getUserIds()) {
            var community = communityService.getCommunityById(invitationDto.getCommunityId());
            var invite = invitationRepository.findByRecipientIdAndCommunityId(userId, community.getId());
            if (invite == null) {
                var invitation = Invitation.builder()
                        .senderId(id)
                        .community(community)
                        .recipientId(userId)
                        .isInvited(true)
                        .isAccepted(false)
                        .isRemoved(false)
                        .date(new Date())
                        .build();
                invitationRepository.save(invitation);
            }else{
                invite.setRemoved(false);
                invite.setAccepted(false);
                invite.setInvited(true);
                invite.setDate(new Date());
                invitationRepository.save(invite);
            }
        }
    }

    @Override
    public List<Invitation> findLoginUserInvitation(Long id) {
        return invitationRepository.findByRecipientIdAndIsInvited(id,true);
    }

    @Transactional
    @Override
    public void findById(Long id) {
        invitationRepository.findById(id).ifPresent(i -> {
            i.setInvited(false);
            i.setAccepted(false);
            i.setRemoved(true);
            invitationRepository.save(i);
        });
    }

    @Transactional
    @Override
    public void acceptedInvitation(Long id, Long communityId) {
        invitationRepository.findById(id).ifPresent(i -> {
            i.setInvited(false);
            i.setRemoved(false);
            i.setAccepted(true);
            invitationRepository.save(i);
        });
       processAcceptInvitation(id,communityId);
    }

    @Override
    public List<Invitation> findByCommunityIdAndIsInvited(Long id, boolean b) {
        return invitationRepository.findByCommunityIdAndIsInvited(id,b);
    }

    public void processAcceptInvitation(Long id,Long communityId){
        var invitation = invitationRepository.findById(id).orElseThrow(() -> new CommunityHubException("Invitation not found exception!!"));
        var user = userService.findById(invitation.getRecipientId());
        var community = communityService.getCommunityById(communityId);
        var user_group = user_groupService.findByUserIdAndCommunityId(user.getId(),community.getId());
        if(user_group == null){
            var groupUser = User_Group.builder()
                    .date(new Date())
                    .user(user)
                    .community(community)
                     .build();
            user_groupService.save(groupUser);
        }
    }

}
