package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.dto.CommentDto;
import com.communityHubSystem.communityHub.dto.MessageDto;
import com.communityHubSystem.communityHub.dto.MessageDtoForCommentReaction;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.*;
import com.communityHubSystem.communityHub.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ReactController {

    private final ReactService reactService;
    private final SimpMessagingTemplate messagingTemplate;
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final NotificationService notificationService;
    private final EventService eventService;
    private final MentionService mentionService;
    private final User_GroupService user_groupService;

    @MessageMapping("/react-message")
    public void processedReactMessage(@Payload MessageDto messageDto) {
        var loginUser = userService.findByStaffId(messageDto.getSender()).orElseThrow(() -> new CommunityHubException("User Name Not Found Exception"));
        var post = postService.findById(messageDto.getPostId());
        var postedUser = userService.findById(post.getUser().getId());
        var isExistedReact = reactService.findByPostIdAndUserId(post.getId(), loginUser.getId());
        System.out.println("EEE" + isExistedReact);
        System.out.println("ya lr : " + messageDto.getType());
        if (!isExistedReact) {
            var react = React.builder()
                    .date(new Date())
                    .type(messageDto.getType())
                    .post(post)
                    .user(loginUser)
                    .build();
            reactService.save(react);
               if(!loginUser.getId().equals(postedUser.getId())){
                   var noti = Notification.builder()
                           .content(messageDto.getContent())
                           .date(new Date())
                           .user(postedUser)
                           .post(post)
                           .react(react)
                           .build();
                   notificationService.save(noti);
                   messagingTemplate.convertAndSendToUser(postedUser.getStaffId(), "/like-private-message", new MessageDto(
                           messageDto.getPostId(),
                           postedUser.getStaffId(),
                           loginUser.getName(),
                           messageDto.getContent(),
                           messageDto.getType(),
                           loginUser.getPhoto(),
                           new Date()
                   ));
               }
        } else {
            var existedReact = reactService.findReactByPostIdAndUserId(post.getId(), loginUser.getId());
            System.out.println(existedReact.getType());
            if (!existedReact.getType().equals(messageDto.getType())) {
                reactService.updatedReact(existedReact.getId(), messageDto.getType());
            }
        }
    }

    @GetMapping("/like-type/{id}")
    @ResponseBody
    public ResponseEntity<?> getAllReactsType(@PathVariable("id") Long id) {
        var staffId = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userService.findByStaffId(staffId).orElseThrow(() -> new CommunityHubException("user name not found exception"));
        var post = postService.findById(id);
        var react = reactService.findReactByPostIdAndUserId(post.getId(), user.getId());
        if (react == null) {
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.ok(react.getType());
        }
    }

    @GetMapping("/like-size/{id}")
    @ResponseBody
    public ResponseEntity<?> getAllReacts(@PathVariable("id") Long id) {
        List<React> reactList = reactService.findByPostId(id);
        var reacts = new ArrayList<>();
        for (React react : reactList) {
            if (react.getComment() == null && react.getReply() == null && react.getType() != Type.OTHER) {
                reacts.add(react);
            }
        }
        System.out.println("sizeForNotComment" + reacts.size());
        return ResponseEntity.status(HttpStatus.OK).body(reacts);
    }

    @MessageMapping("/comment-message")
    public void processedCommentMessage(@Payload CommentDto commentDto) {
        var loginUser = userService.findByStaffId(commentDto.getSender()).orElseThrow(() -> new CommunityHubException("User Name Not Found Exception"));
        var post = postService.findById(commentDto.getPostId());
        var postedUser = userService.findById(post.getUser().getId());
        var comment = Comment.builder()
                .content(commentDto.getContent())
                .localDateTime(new Date())
                .post(post)
                .user(loginUser)
                .build();
        var cmsvg = commentService.save(comment);
        if(!loginUser.getId().equals(postedUser.getId())) {
            var noti = Notification.builder()
                    .content(commentDto.getContent())
                    .date(new Date())
                    .user(postedUser)
                    .post(post)
                    .comment(comment)
                    .build();
            notificationService.save(noti);
            messagingTemplate.convertAndSend("/user/all/comment-private-message", new CommentDto(
                    commentDto.getPostId(),
                    cmsvg.getId(),
                    commentDto.getReplyId(),
                    postedUser.getStaffId(),
                    loginUser.getName(),
                    commentDto.getContent(),
                    loginUser.getPhoto(),
                    new Date()
            ));
        }else{
            String staffId = "99-00000";
            messagingTemplate.convertAndSend("/user/all/comment-private-message", new CommentDto(
                    commentDto.getPostId(),
                    cmsvg.getId(),
                    commentDto.getReplyId(),
                    staffId,
                    loginUser.getName(),
                    commentDto.getContent(),
                    loginUser.getPhoto(),
                    new Date()
            ));
        }
    }

    @MessageMapping("/comment-reply-message")
    public void processedMessageForReply(@Payload CommentDto commentDto) {
        var loginUser = userService.findByStaffId(commentDto.getSender()).orElseThrow(() -> new CommunityHubException("User Name Not Found Exception"));
        if (commentDto.getCommentId() != null) {
            var comment = commentService.findById(commentDto.getCommentId());
            var commentPost = postService.findById(comment.getPost().getId());
            var user = userService.findById(comment.getUser().getId());
            if (comment != null) {
                var reply = Reply.builder()
                        .localDateTime(new Date())
                        .content(commentDto.getContent())
                        .user(loginUser)
                        .comment(comment)
                        .build();
                replyService.save(reply);
                if(!loginUser.getId().equals(user.getId())) {
                    var noti = Notification.builder()
                            .content(commentDto.getContent())
                            .date(new Date())
                            .user(user)
                            .post(commentPost)
                            .comment(comment)
                            .reply(reply)
                            .build();
                    notificationService.save(noti);
                    messagingTemplate.convertAndSend("/user/all/comment-reply-private-message", new CommentDto(
                            commentPost.getId(),
                            commentDto.getCommentId(),
                            commentDto.getReplyId(),
                            user.getStaffId(),
                            loginUser.getName(),
                            commentDto.getContent(),
                            loginUser.getPhoto(),
                            new Date()
                    ));
                }else{
                    String staffId = "99-00000";
                    messagingTemplate.convertAndSend("/user/all/comment-reply-private-message", new CommentDto(
                            commentPost.getId(),
                            commentDto.getCommentId(),
                            commentDto.getReplyId(),
                            staffId,
                            loginUser.getName(),
                            commentDto.getContent(),
                            loginUser.getPhoto(),
                            new Date()
                    ));
                }
            }
        } else {
            var reply = replyService.findById(commentDto.getReplyId());
            var comment = commentService.findById(reply.getComment().getId());
            var commentPost = postService.findById(comment.getPost().getId());
            var user = userService.findById(reply.getUser().getId());
            if (reply != null) {
                var savedReply = Reply.builder()
                        .localDateTime(new Date())
                        .content(commentDto.getContent())
                        .user(loginUser)
                        .comment(comment)
                        .build();
                replyService.save(savedReply);
                if(!loginUser.getId().equals(user.getId())) {
                    var noti = Notification.builder()
                            .content(commentDto.getContent())
                            .date(new Date())
                            .user(user)
                            .post(commentPost)
                            .comment(comment)
                            .reply(savedReply)
                            .build();
                    notificationService.save(noti);
                    messagingTemplate.convertAndSend( "/user/all/comment-reply-private-message", new CommentDto(
                            commentPost.getId(),
                            comment.getId(),
                            commentDto.getReplyId(),
                            user.getStaffId(),
                            loginUser.getName(),
                            commentDto.getContent(),
                            loginUser.getPhoto(),
                            new Date()
                    ));
                }else{
                    String staffId = "99-00000";
                    messagingTemplate.convertAndSend( "/user/all/comment-reply-private-message", new CommentDto(
                            commentPost.getId(),
                            comment.getId(),
                            commentDto.getReplyId(),
                            staffId,
                            loginUser.getName(),
                            commentDto.getContent(),
                            loginUser.getPhoto(),
                            new Date()
                    ));
                }
            }
        }
    }


    @GetMapping("/getAll-comment/{id}")
    @ResponseBody
    public ResponseEntity<List<Reply>> getComments(@PathVariable("id") Long id) {
        List<Reply> replyList = replyService.getAllRepliesByCommentId(id);
        if (replyList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(Collections.EMPTY_LIST);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(replyList);
        }
    }

    @GetMapping("/getComment/{id}")
    @ResponseBody
    public ResponseEntity<List<Comment>> getAllComments(@PathVariable("id") Long id) {
        List<Comment> commentList = commentService.findCommentsByPostId(id);
        if (commentList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(Collections.EMPTY_LIST);
        } else {
//            return ResponseEntity.status(HttpStatus.OK).body( commentList.stream()
//                    .map(c -> modelMapper.map(c, CommentDtoForNotification.class))
//                    .collect(Collectors.toList()));
            return ResponseEntity.status(HttpStatus.OK).body(commentList);
        }
    }

    @GetMapping("/get-userData/{id}")
    @ResponseBody
    public ResponseEntity<?> getUserByStaffId(@PathVariable("id") String id) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByStaffId(id.trim()).orElseThrow(() -> new CommunityHubException("User Not found exception!")));
    }

    @GetMapping("/get-userPostsData/{id}")
    @ResponseBody
    public ResponseEntity<?> getUserByStaffIdForPost(@PathVariable("id") String id) {
        var user = userService.findByStaffId(id).orElseThrow(() -> new CommunityHubException("User Name not found exception"));
        List<Post> posts = postService.findPostByUserId(user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(posts);
    }

    @GetMapping("/comment-size/{id}")
    @ResponseBody
    public ResponseEntity<?> getCommentSizeByPost(@PathVariable("id") Long id) {
        List<Comment> commentList = commentService.findCommentsByPostId(id);
        int commentSize = 0;
        if (commentList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(Collections.EMPTY_LIST);
        } else {
            commentSize = commentList.size();
            for (Comment comment : commentList) {
                List<Reply> replyList = replyService.getAllRepliesByCommentId(comment.getId());
                commentSize += replyList.size();
            }
            System.out.println("comemntsidfsdf" + commentSize);
            return ResponseEntity.status(HttpStatus.OK).body(commentSize);
        }
    }

    @GetMapping("/remove-like-type/{id}")
    @ResponseBody
    public ResponseEntity<?> removeReaction(@PathVariable("id") Long id) {
        var staffId = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userService.findByStaffId(staffId).orElseThrow(() -> new CommunityHubException("user name not found exception"));
        var post = postService.findById(id);
        var react = reactService.findReactByPostIdAndUserId(id, user.getId());
         if(react != null){
             reactService.removeReactType(react.getId());
         }
        System.out.println("Ya twar p");
        return new ResponseEntity(HttpStatus.OK);
    }


    @MessageMapping("/like-commentReact-message")
    public void processLikedCommentMessage(@Payload MessageDtoForCommentReaction messageDto) {
        var loginUser = userService.findByStaffId(messageDto.getSender().trim()).orElseThrow(() -> new CommunityHubException("user name not found exception"));
        var comment = commentService.findById(messageDto.getCommentId());
        var user = userService.findById(comment.getUser().getId());
        var post = postService.findById(comment.getPost().getId());
        var isReact = reactService.findReactByUserIdAndCommentId(loginUser.getId(), messageDto.getCommentId(), post.getId());
        if (isReact == null) {
            var react = React.builder()
                    .date(new Date())
                    .type(messageDto.getType())
                    .post(post)
                    .user(loginUser)
                    .comment(comment)
                    .build();
            reactService.save(react);
      if(!loginUser.getId().equals(user.getId())) {
          var noti = Notification.builder()
                  .date(new Date())
                  .user(loginUser)
                  .post(post)
                  .comment(comment)
                  .react(react)
                  .build();
          notificationService.save(noti);
          messagingTemplate.convertAndSendToUser(comment.getUser().getStaffId(), "/like-private-message", new MessageDto(
                  messageDto.getPostId(),
                  comment.getUser().getStaffId(),
                  loginUser.getName(),
                  messageDto.getContent(),
                  messageDto.getType(),
                  loginUser.getPhoto(),
                  new Date()
          ));
      }
        } else {
            if (isReact.getType() == null) {
                reactService.modifyReact(isReact.getId(), messageDto.getType());
            } else if (!isReact.getType().equals(messageDto.getType())) {
                reactService.modifyReact(isReact.getId(), messageDto.getType());
            }
        }
    }


    @GetMapping("/comment-type-react/{id}/{userId}/{postId}")
    @ResponseBody
    public ResponseEntity<?> getCommentType(@PathVariable("id") Long id,
                                            @PathVariable("userId") Long userId,
                                            @PathVariable("postId") Long postId) {
        var react = reactService.findReactByUserIdAndPostIdAndCommentId(userId, postId, id);
        if (react == null) {
            return ResponseEntity.ok(HttpEntity.EMPTY);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(react);
        }
    }


    @MessageMapping("/like-replyReact-message")
    public void processReactReplyMessage(@Payload MessageDtoForCommentReaction messageDtoForCommentReaction) {
        var loginUser = userService.findByStaffId(messageDtoForCommentReaction.getSender().trim()).orElseThrow(() -> new CommunityHubException("user name not found exception"));
        var reply = replyService.findById(messageDtoForCommentReaction.getPostId());
        var comment = commentService.findById(messageDtoForCommentReaction.getCommentId());
        var user = userService.findById(reply.getUser().getId());
        var post = postService.findById(comment.getPost().getId());
        var isReact = reactService.getReact(loginUser.getId(), post.getId(), comment.getId(), reply.getId());
        if (isReact == null) {
            var react = React.builder()
                    .date(new Date())
                    .type(messageDtoForCommentReaction.getType())
                    .post(post)
                    .user(loginUser)
                    .comment(comment)
                    .reply(reply)
                    .build();
            reactService.save(react);
            if(!loginUser.getId().equals(user.getId())) {
                var noti = Notification.builder()
                        .date(new Date())
                        .user(loginUser)
                        .post(post)
                        .reply(reply)
                        .react(react)
                        .build();
                notificationService.save(noti);
                messagingTemplate.convertAndSendToUser(reply.getUser().getStaffId(), "/like-private-message", new MessageDto(
                        messageDtoForCommentReaction.getPostId(),
                        comment.getUser().getStaffId(),
                        loginUser.getName(),
                        messageDtoForCommentReaction.getContent(),
                        messageDtoForCommentReaction.getType(),
                        loginUser.getPhoto(),
                        new Date()
                ));
            }
        } else {
            if (isReact.getType() == null) {
                reactService.modifyReact(isReact.getId(), messageDtoForCommentReaction.getType());
            } else if (!isReact.getType().equals(messageDtoForCommentReaction.getType())) {
                reactService.modifyReact(isReact.getId(), messageDtoForCommentReaction.getType());
            }
        }
    }


    @GetMapping("/reply-type-react/{id}/{userId}/{replyId}")
    @ResponseBody
    public ResponseEntity<?> getReplyType(@PathVariable("id") Long id,
                                            @PathVariable("userId") Long userId,
                                            @PathVariable("replyId")Long replyId) {
        var reply = replyService.findById(replyId);
        var post = postService.findById(reply.getComment().getPost().getId());
        var react = reactService.getReact(userId, post.getId(), id,reply.getId());
        if (react == null) {
            return ResponseEntity.ok(HttpEntity.EMPTY);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(react);
        }
    }


    @MessageMapping("/react-event-message")
    public void precessEventReactType(@Payload MessageDto messageDto){
        var loginUser = userService.findByStaffId(messageDto.getSender()).orElseThrow(() -> new CommunityHubException("User Name Not Found Exception"));
         var event = eventService.findById(messageDto.getPostId());
         var postedUser = userService.findById(event.getUser().getId());
         var isExistedEventType = reactService.findByUserIdAndEventId(loginUser.getId(),event.getId());
    if(isExistedEventType == null){
        var react = React.builder()
                .date(new Date())
                .type(messageDto.getType())
                .event(event)
                .user(loginUser)
                .build();
        reactService.save(react);
        if(!loginUser.getId().equals(postedUser.getId())) {
            var noti = Notification.builder()
                    .content(messageDto.getContent())
                    .date(new Date())
                    .user(postedUser)
                    .event(event)
                    .react(react)
                    .build();
            notificationService.save(noti);
            messagingTemplate.convertAndSendToUser(postedUser.getStaffId(), "/like-private-message", new MessageDto(
                    messageDto.getPostId(),
                    postedUser.getStaffId(),
                    loginUser.getName(),
                    messageDto.getContent(),
                    messageDto.getType(),
                    loginUser.getPhoto(),
                    new Date()
            ));
        }
    }else{
        if (!isExistedEventType.getType().equals(messageDto.getType())) {
            reactService.updatedReact(isExistedEventType.getId(), messageDto.getType());
        }
    }

    }


    @GetMapping("/remove-like-eventReact-type/{id}")
    public ResponseEntity<?> eventRemoveReaction(@PathVariable("id")Long id){
        var staffId = SecurityContextHolder.getContext().getAuthentication().getName();
        var user = userService.findByStaffId(staffId).orElseThrow(() -> new CommunityHubException("user name not found exception"));
        var event = eventService.findById(id);
        var react = reactService.findByUserIdAndEventId(user.getId(),event.getId());
        if(react != null){
            reactService.removeReactType(react.getId());
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/get-mentionUser/{id}")
    public ResponseEntity<Mention> getMentionWithId(@PathVariable("id")Long id){
        return ResponseEntity.status(HttpStatus.OK).body(mentionService.getMention(id));
    }

    @GetMapping("/getData-mention/{id}")
    public ResponseEntity<User> getMentionUser(@PathVariable("id")Long id){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
    }

    @GetMapping("/get-mentionUsers-group/{id}")
    public ResponseEntity<List<User>> getMentionUsersForPost(@PathVariable("id")Long id){
        return ResponseEntity.status(HttpStatus.OK).body(getMentionsUsersByPostId(id));
    }

    @GetMapping("/get-event-postedUser/{id}")
    public ResponseEntity<User> getEventPostedUser(@PathVariable("id")String id){
        return ResponseEntity.status(HttpStatus.OK).body(userService.findByStaffId(id).orElseThrow(()->new CommunityHubException("User Name not found exception!")));
    }

    @GetMapping("/get-eventGroup-postedUser/{id}")
    public ResponseEntity<List<User>> getEventPostedUserWithinGroup(@PathVariable("id")Long id){
       List<User_Group> user_groups = user_groupService.findByCommunityId(id);
      List<User> users = new ArrayList<>();
       for(User_Group user_group:user_groups){
           var user = userService.findById(user_group.getUser().getId());
           if(!user.getRole().equals(User.Role.ADMIN)){
               users.add(user);
           }
       }
        return ResponseEntity.status(HttpStatus.OK).body(users);

    }


    public List<User> getMentionsUsersByPostId(Long id) {
        var post = postService.findById(id);
        var loginUser = userService.findByStaffId(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CommunityHubException("User name not found exception!"));

        if (post == null) {
            throw new CommunityHubException("Post not found!");
        }

        List<User> users = new ArrayList<>();

        if (post.getAccess().equals(Access.PUBLIC)) {
            users = userService.getAllActiveUser().stream()
                    .filter(user -> !user.getId().equals(loginUser.getId()))
                    .collect(Collectors.toList());
        } else {
            var userGroups = user_groupService.findByCommunityId(post.getUserGroup().getCommunity().getId());
            for (User_Group userGroup : userGroups) {
                var user = userService.findById(userGroup.getUser().getId());
                if (user != null && !user.getRole().equals(User.Role.ADMIN) && !user.getId().equals(loginUser.getId())) {
                    users.add(user);
                }
            }
        }
        return users;
    }


}






















































