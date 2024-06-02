package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.dto.InvitationDto;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.Community;
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
            var invite = invitationRepository.findByRecipientIdAndCommunityIdAndIsRequested(userId, community.getId(), false);
            if (invite == null) {
                var invitation = Invitation.builder()
                        .senderId(id)
                        .community(community)
                        .recipientId(userId)
                        .isInvited(true)
                        .isAccepted(false)
                        .isRemoved(false)
                        .isRequested(false)
                        .date(new Date())
                        .build();
                invitationRepository.save(invitation);
            } else {
                invite.setRemoved(false);
                invite.setAccepted(false);
                invite.setInvited(true);
                invite.setRequested(false);
                invite.setDate(new Date());
                invitationRepository.save(invite);
            }
        }
    }

    @Override
    public List<Invitation> findLoginUserInvitation(Long id) {
        return invitationRepository.findByRecipientIdAndIsInvited(id, true);
    }

    @Transactional
    @Override
    public void findById(Long id) {
        invitationRepository.findById(id).ifPresent(i -> {
            i.setInvited(false);
            i.setAccepted(false);
            i.setRequested(false);
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
            i.setRequested(false);
            i.setAccepted(true);
            invitationRepository.save(i);
        });
        processAcceptInvitation(id, communityId);
    }

    @Override
    public List<Invitation> findByCommunityIdAndIsInvited(Long id, boolean b) {
        return invitationRepository.findByCommunityIdAndIsInvited(id, b);
    }

    @Override
    public List<Invitation> findByCommunityIdAndIsRemoved(Long id, boolean b) {
        return invitationRepository.findByCommunityIdAndIsRemovedAndIsRequested(id, b, false);
    }


    @Transactional
    @Override
    public void requestedInvitation(User user, User loginUser, Community community) {
        var invite = invitationRepository.findByRecipientIdAndCommunityIdAndIsRequested(user.getId(), community.getId(), true);
        if(invite == null){
            var invitation = Invitation.builder()
                    .senderId(loginUser.getId())
                    .community(community)
                    .recipientId(user.getId())
                    .isInvited(false)
                    .isAccepted(false)
                    .isRemoved(false)
                    .isRequested(true)
                    .date(new Date())
                    .build();
            invitationRepository.save(invitation);
        }else{
            invite.setRemoved(false);
            invite.setAccepted(false);
            invite.setInvited(false);
            invite.setRequested(true);
            invite.setDate(new Date());
            invitationRepository.save(invite);
        }

    }

    @Transactional
    @Override
    public void requestAcceptedInvitation(Long id) {
        var invitation = invitationRepository.findById(id).orElseThrow(() -> new CommunityHubException("Invitation not found exception!"));
        var user = userService.findById(invitation.getSenderId());
        var community = communityService.findById(invitation.getCommunity().getId());
        invitationRepository.findById(id).ifPresent(i -> {
            i.setInvited(false);
            i.setRemoved(false);
            i.setRequested(false);
            i.setAccepted(true);
            invitationRepository.save(i);
        });
        var userGroup = User_Group.builder()
                .user(user)
                .community(community)
                .date(new Date())
                .build();
        user_groupService.save(userGroup);
    }

    @Transactional
    @Override
    public void requestDeniedInvitation(Long id) {
        invitationRepository.findById(id).ifPresent(i -> {
            i.setInvited(false);
            i.setAccepted(false);
            i.setRequested(true);
            i.setRemoved(true);
            invitationRepository.save(i);
        });
    }

    @Override
    public List<Invitation> findByCommunityIdAndIsRemovedAndIsRequested(Long id, boolean b, boolean b1) {
        return invitationRepository.findByCommunityIdAndIsRemovedAndIsRequested(id, b, b1);
    }

    @Override
    public List<Invitation> findInvitationsByCommunityIdAndIsRemovedAndIsRequested(Long id, boolean b, boolean b1) {
        return invitationRepository. findByCommunityIdAndIsRemovedAndIsRequested(id,b,b1);
    }

    @Override
    public List<Invitation> findByRecipientId(Long uId) {
        return invitationRepository.findByRecipientId(uId);
    }

    @Override
    public List<Invitation> findBySenderId(Long uId) {
        return invitationRepository.findBySenderId(uId);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        invitationRepository.deleteById(id);
    }

    public void processAcceptInvitation(Long id, Long communityId) {
        var invitation = invitationRepository.findById(id).orElseThrow(() -> new CommunityHubException("Invitation not found exception!!"));
        var user = userService.findById(invitation.getRecipientId());
        var community = communityService.getCommunityById(communityId);
        var user_group = user_groupService.findByUserIdAndCommunityId(user.getId(), community.getId());
        if (user_group == null) {
            var groupUser = User_Group.builder()
                    .date(new Date())
                    .user(user)
                    .community(community)
                    .build();
            user_groupService.save(groupUser);
        }
    }

}
