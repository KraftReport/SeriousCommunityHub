package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.dto.InvitationDto;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.User;
import com.communityHubSystem.communityHub.services.InvitationService;
import com.communityHubSystem.communityHub.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class InvitationController {

    private final InvitationService invitationService;
    private final UserService userService;


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


    public User getLoginUser() {
        var user = userService.findByStaffId(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new CommunityHubException("User Name Not Found Exception"));
        if (user != null) {
            return user;
        }
        return null;
    }

}
