package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.dto.MessageDto;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.React;
import com.communityHubSystem.communityHub.services.PostService;
import com.communityHubSystem.communityHub.services.ReactService;
import com.communityHubSystem.communityHub.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReactController {

    private final ReactService reactService;
    private final SimpMessagingTemplate messagingTemplate;
    private final PostService postService;
    private final UserService userService;

    @MessageMapping("/react-message")
    public void processedReactMessage(@Payload MessageDto messageDto) {
        var loginUser = userService.findByStaffId(messageDto.getSender()).orElseThrow(() -> new CommunityHubException("User Name Not Found Exception"));
        var post = postService.findById(messageDto.getPostId());
        var postedUser = userService.findById(post.getUser().getId());
        var isExistedReact = reactService.findByPostIdAndUserId(post.getId(),loginUser.getId());
        System.out.println("EEE"+isExistedReact);
        if(!isExistedReact) {
            var react = React.builder()
                    .date(new Date())
                    .type(messageDto.getType())
                    .post(post)
                    .user(loginUser)
                    .build();
            reactService.save(react);
        }
        messagingTemplate.convertAndSendToUser(postedUser.getStaffId(), "/like-private-message", new MessageDto(
                messageDto.getPostId(),
                loginUser.getName(),
                messageDto.getContent(),
                messageDto.getType(),
                new Date()
        ));
    }

    @GetMapping("/like-size/{id}")
    @ResponseBody
    public ResponseEntity<?> getAllReacts(@PathVariable("id")Long id){
        List<React> reactList = reactService.findByPostId(id);
        return ResponseEntity.status(HttpStatus.OK).body(reactList.size());
    }
}
