package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.dto.InvitationDto;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.Invitation;
import com.communityHubSystem.communityHub.models.User;
import com.communityHubSystem.communityHub.services.CommunityService;
import com.communityHubSystem.communityHub.services.InvitationService;
import com.communityHubSystem.communityHub.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class InvitationController {

    private final InvitationService invitationService;
    private final UserService userService;
    private final CommunityService communityService;


    @PutMapping("/invitationSend")
    public ResponseEntity<Map<String,String>> processedInvitation(@ModelAttribute InvitationDto invitationDto) {
        var loginUser = getLoginUser();
         invitationService.save(loginUser.getId(),invitationDto);
         Map<String,String> response = new HashMap<>();
         response.put("message","invite successfully!");
        System.out.println("DSDFSDF"+invitationDto.getCommunityId() + " " + invitationDto.getUserIds().size());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/invited-user-count")
    public ResponseEntity<?> getInvitedUserNotiCount(){
        var loginUser = getLoginUser();
        var invitationCount = invitationService.findLoginUserInvitation(loginUser.getId());
        if(!invitationCount.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(invitationCount.size());
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(Collections.EMPTY_LIST);
        }
    }


    @GetMapping("/invited-user-display")
    public ResponseEntity<?> getInvitedUserForDisplay(){
        var user = getLoginUser();
        var invitationList = invitationService.findLoginUserInvitation(user.getId());
        if(!invitationList.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(invitationList);
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(Collections.EMPTY_LIST);
        }
    }


    @GetMapping("/accept-invitation/{id}/{communityId}")
    public ResponseEntity<Map<String,String>> acceptInvitation(@PathVariable("id")Long id,@PathVariable("communityId")Long communityId){
        invitationService.acceptedInvitation(id,communityId);
        Map<String,String> response = new HashMap<>();
        response.put("message","Accepted Successfully and you are now a member of group called");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/reject-invitation/{id}")
    public ResponseEntity<Map<String,String>> rejectInvitation(@PathVariable("id")Long id){
         invitationService.findById(id);
         Map<String,String> response = new HashMap<>();
         response.put("message","Rejected Successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/get-invited-user/{id}")
    public ResponseEntity<User> getInvitedUser(@PathVariable("id")Long id){
        var user = userService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }


    public User getLoginUser() {
        var user = userService.findByStaffId(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new CommunityHubException("User Name Not Found Exception"));
        if (user != null) {
            return user;
        }
        return null;
    }

    @GetMapping("/get-invitedPendingUser/{id}")
    public ResponseEntity<List<Long>> getInvitedPendingUser(@PathVariable("id") Long id) {
        List<Long> userList = new ArrayList<>();
        List<Invitation> invitationList = invitationService.findByCommunityIdAndIsInvited(id, true);

        for (Invitation invitation : invitationList) {
            User user = userService.findById(invitation.getRecipientId());
            if (user != null) {
                userList.add(user.getId());
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    @GetMapping("/get-rejectedUser/{id}")
    public ResponseEntity<List<Long>> getRejectedUser(@PathVariable("id")Long id){
        List<Long> userList = new ArrayList<>();
        List<Invitation> invitationList = invitationService.findByCommunityIdAndIsRemoved(id, true);

        for (Invitation invitation : invitationList) {
            User user = userService.findById(invitation.getRecipientId());
            if (user != null) {
                userList.add(user.getId());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    //for user request start
    @GetMapping("/request-invitation/{id}")
    public ResponseEntity<Map<String,String>> getRequestInvitation(@PathVariable("id")Long id){
        Map<String,String> res = new HashMap<>();
        var community = communityService.findById(id);
        var loginUser = getLoginUser();
        var user = userService.findByName(community.getOwnerName().trim());
        if(user != null){
            invitationService.requestedInvitation(user,loginUser,community);
            res.put("message","Requested Successfully!Admin team will response later.");
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }else{
            res.put("message","There is no owner in this group! You can't request now!");
            return ResponseEntity.status(HttpStatus.OK).body(res);
        }
    }

    @GetMapping("/request-accept-invitation/{id}")
    public ResponseEntity<Map<String,String>> requestAcceptedInvitation(@PathVariable("id")Long id){
        Map<String,String> res = new HashMap<>();
        invitationService.requestAcceptedInvitation(id);
        res.put("message","Accepted Successfully");
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/request-deny-invitation/{id}")
    public ResponseEntity<Map<String,String>> requestDeniedInvitation(@PathVariable("id")Long id){
        Map<String,String> response = new HashMap<>();
        response.put("message","Rejected Successfully");
        invitationService.requestDeniedInvitation(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/get-requestDeniedUser/{id}")
    public ResponseEntity<List<Long>> getRejectedUserForAcceptInvitation(@PathVariable("id")Long id){
        List<Long> userList = new ArrayList<>();
        List<Invitation> invitationList = invitationService.findByCommunityIdAndIsRemovedAndIsRequested(id, true,true);

        for (Invitation invitation : invitationList) {
            User user = userService.findById(invitation.getRecipientId());
            if (user != null) {
                userList.add(user.getId());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    @GetMapping("/get-requestPendingUser/{id}")
    public ResponseEntity<List<Long>> getPendingUserForAcceptInvitation(@PathVariable("id")Long id){
        List<Long> userList = new ArrayList<>();
        List<Invitation> invitationList = invitationService.findByCommunityIdAndIsRemovedAndIsRequested(id, false,true);

        for (Invitation invitation : invitationList) {
            User user = userService.findById(invitation.getRecipientId());
            if (user != null) {
                userList.add(user.getId());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    @GetMapping("/request-groupOwner-invitation/{id}")
    public ResponseEntity<?> getRequestUsersInvitationList(@PathVariable("id")Long id){
        var invitationList = invitationService.findInvitationsByCommunityIdAndIsRemovedAndIsRequested(id,false,true);
        if(!invitationList.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(invitationList);
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(Collections.EMPTY_LIST);
        }

    }

    @GetMapping("/request-user-display/{id}")
    public ResponseEntity<List<Invitation>> getRequestedInvitations(@PathVariable("id")Long id){
        var invitationList = invitationService.findByCommunityIdAndIsRemovedAndIsRequested(id,false,true);
        if(!invitationList.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(invitationList);
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(Collections.EMPTY_LIST);
        }
    }

    @GetMapping("/check-loginUser")
    public ResponseEntity<?> checkVisitorForLoginUser(){
       return ResponseEntity.status(HttpStatus.OK).body(getLoginUser());
    }
    //for user request end


    public List<User> getActiveUsersForInvitation(){
        return userService.getAllActiveUser();
    }
}
