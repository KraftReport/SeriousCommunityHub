package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.*;
import com.communityHubSystem.communityHub.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class RecordController {

    private final UserService userService;
    private final PostService postService;
    private final ReactService reactService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final CommunityService communityService;
    private final User_GroupService user_groupService;


    @GetMapping("/record-user-post-withinOneMonth")
    public ResponseEntity<Post> getOnePostForLoginUserWithinOneMonth() {
        return ResponseEntity.status(HttpStatus.OK).body(getOneTrendyPostForLoginUserWithinOneMonth());
    }

    @GetMapping("/record-user-post-withinOneYear")
    public ResponseEntity<Post> getOnePostForLoginUserWithinOneYear() {
        return ResponseEntity.status(HttpStatus.OK).body(getOneTrendyPostForLoginUserWithinOneYear());
    }

    @GetMapping("/record-user-trendyReact-post-countWithinOneMonth")
    public ResponseEntity<Long> getOneTrendyPostForReactCountWithinOneMonth(){
        return ResponseEntity.status(HttpStatus.OK).body(getReactTotalSizeForOneTrendyPostsWithinOneMonth());
    }

    @GetMapping("/record-user-trendyReact-post-countWithOneYear")
    public ResponseEntity<Long> getOneTrendyPostForReactCountWithinOneYear(){
        return ResponseEntity.status(HttpStatus.OK).body(getReactTotalSizeForOneTrendyPostsWithinOneYear());
    }

    @GetMapping("/record-user-trendyComment-post-countWithOneMonth")
    public ResponseEntity<Long> getOneTrendyPostForCommentSizeWithinOneMonth(){
        return ResponseEntity.status(HttpStatus.OK).body(getCommentTotalSizeForOneTrendyPostsWithinOneMonth());
    }

    @GetMapping("/record-user-trendyComment-post-countWithOneYear")
    public ResponseEntity<Long> getOneTrendyPostForCommentSizeWithinOneYear(){
        return ResponseEntity.status(HttpStatus.OK).body(getCommentTotalSizeForOneTrendyPostsWithinOneYear());
    }

    @GetMapping("/record-group-post-withinOneMonth")
    public ResponseEntity<List<Post>> getOnePostForGroupWithinOneMonth() {
        return ResponseEntity.status(HttpStatus.OK).body(getTrendyPostsForGroupWithinOneMonth());
    }

    @GetMapping("/record-group-post-withinOneYear")
    public ResponseEntity<List<Post>> getOnePostForGroupWithinOneYear() {
        return ResponseEntity.status(HttpStatus.OK).body(getTrendyPostsForGroupWithinOneYear());
    }

    @GetMapping("/record-group-trendyCommunity")
    public ResponseEntity<Community> getTrendyCommunity() {
        return ResponseEntity.status(HttpStatus.OK).body(getCommunityByMostMemberList());
    }

    @GetMapping("/record-group-trendyCommunityMemberList")
    public ResponseEntity<List<User_Group>> getTrendyCommunityMemberList(){
        return ResponseEntity.status(HttpStatus.OK).body(getAllUserGroupWithCommunityId());
    }


    @GetMapping("/record-groupPostReacts-listForOneMonth")
    public ResponseEntity<Long> getTotalReactsForTrendyCommunityWithinOneMonth(){
        return ResponseEntity.status(HttpStatus.OK).body(getReactSizeForMostCommunityMembersWithinOneMonth());
    }
    @GetMapping("/record-groupPostReacts-listForOneYear")
    public ResponseEntity<Long> getTotalReactsForTrendyCommunityWithinOneYear(){
        return ResponseEntity.status(HttpStatus.OK).body(getReactSizeForMostCommunityMembersWithinOneYear());
    }

    @GetMapping("/record-groupPostComments-listForOneMonth")
    public ResponseEntity<Long> getCommentTotalSizeForTrendyCommunityWithinOneMonth(){
        return ResponseEntity.status(HttpStatus.OK).body(getReactSizeForMostCommunityMembersWithinOneYear());
    }

    @GetMapping("/record-groupPostComments-listForOneYear")
    public ResponseEntity<Long> getCommentTotalSizeForTrendyCommunityWithinOneYear(){
        return ResponseEntity.status(HttpStatus.OK).body(getReactSizeForMostCommunityMembersWithinOneYear());
    }


    // for login user trendy post start
    public User loginUser() {
        var user = userService.findByStaffId(SecurityContextHolder
                .getContext().getAuthentication().getName())
                .orElseThrow(() ->
                        new CommunityHubException("User Name not found exception!"));
        return user;
    }

    public List<Post> getAllPostsForLoginUser() {
        var loginUser = loginUser();
        List<Post> postList = postService.findAllPostByIsDeleted(false, loginUser.getId());
        return postList;
    }

    public List<Post> getTrendyPostForLoginUserInOneMonth() {
        var loginUser = loginUser();
        LocalDate currentDate = LocalDate.now();
        LocalDate startDateOfMonth = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), 1);
        List<Post> postList = getAllPostsForLoginUser();
        if (!postList.isEmpty()) {
            return postList.stream()
                    .filter(post -> {
                        Date postDate = post.getCreatedDate();
                        LocalDate localDate = postDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        return localDate.isAfter(startDateOfMonth);
                    })
                    .toList();
        } else {
            return null;
        }
    }

    public List<Post> getTrendyPostForLoginUserInOneYear() {
        var loginUser = loginUser();
        LocalDate currentDate = LocalDate.now();
        LocalDate startDateOfYear = LocalDate.of(currentDate.getYear(), 1, 1);
        List<Post> postList = getAllPostsForLoginUser();
        if (!postList.isEmpty()) {
            return postList.stream()
                    .filter(post -> {
                        Date postDate = post.getCreatedDate();
                        LocalDate localDate = postDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        return !localDate.isBefore(startDateOfYear);
                    })
                    .toList();
        } else {
            return null;
        }
    }

    public Long getReactTotalSizeForPostsWithinOneMonth() {
        Long longList = 0L;
        List<Post> postList = getTrendyPostForLoginUserInOneMonth();
        if (!postList.isEmpty()) {
            for (Post post : postList) {
                Long reactSize = reactService.finReactByCommentIdIsNullAndPostIdAndReplyIdIsNull(post.getId());
                longList += reactSize;
            }
            return longList;
        }
        return longList;
    }

    public Long getReactTotalSizeForPostsWithinOneYear() {
        Long longList = 0L;
        List<Post> postList = getTrendyPostForLoginUserInOneYear();
        if (!postList.isEmpty()) {
            for (Post post : postList) {
                Long reactSize = reactService.finReactByCommentIdIsNullAndPostIdAndReplyIdIsNull(post.getId());
                longList += reactSize;
            }
            return longList;
        } else {
            return longList;
        }
    }


    public Long getReactTotalSizeForAllPost() {
        Long longList = 0L;
        List<Post> postList = getAllPostsForLoginUser();
        if (!postList.isEmpty()) {
            for (Post post : postList) {
                List<React> reactList = reactService.findReactByCommentIdAndPostId(post.getId());
                longList += reactList.size();
            }
            return longList;
        } else {
            return longList;
        }
    }

    public Long getCommentTotalSizeForPostsWithinOneMonth() {
        Long longList = 0L;
        List<Post> postList = getTrendyPostForLoginUserInOneMonth();
        if (!postList.isEmpty()) {
            List<Comment> commentSize = new ArrayList<>();
            for (Post post : postList) {
                Long comment = commentService.findCommentSizeByPostId(post.getId());
                List<Comment> commentList = commentService.findCommentsByPostId(post.getId());
                commentSize.addAll(commentList);
                longList += comment;
            }
            for (Comment comment : commentSize) {
                Long reply = replyService.findReplySizeByCommentId(comment.getId());
                longList += reply;
            }
            return longList;
        } else {
            return longList;
        }
    }


    public Long getCommentTotalSizeForPostsWithinOneYear() {
        Long longList = 0L;
        List<Post> postList = getTrendyPostForLoginUserInOneYear();
        if (!postList.isEmpty()) {
            List<Comment> commentCount = new ArrayList<>();
            for (Post post : postList) {
                Long comment = commentService.findCommentSizeByPostId(post.getId());
                List<Comment> commentList = commentService.findCommentsByPostId(post.getId());
                commentCount.addAll(commentList);
                longList += comment;
            }
            for (Comment comment : commentCount) {
                Long reply = replyService.findReplySizeByCommentId(comment.getId());
                longList += reply;
            }
            return longList;
        } else {
            return longList;
        }
    }


    public Long getCommentTotalSizeForAllPost() {
        Long longList = 0L;
        List<Post> postList = getAllPostsForLoginUser();
        if (!postList.isEmpty()) {
            List<Comment> comments = new ArrayList<>();
            for (Post post : postList) {
                List<Comment> commentList = commentService.findCommentsByPostId(post.getId());
                comments.addAll(commentList);
                longList += commentList.size();
            }
            for (Comment comment : comments) {
                List<Reply> replyList = replyService.getAllRepliesByCommentId(comment.getId());
                longList += replyList.size();
            }
            return longList;
        } else {
            return longList;
        }
    }


    public Long getReactTotalSizeForOneTrendyPostsWithinOneMonth() {
        Long longList = 0L;
        Post post = getOneTrendyPostForLoginUserWithinOneMonth();
        if (post != null) {
            Long reactSize = reactService.finReactByCommentIdIsNullAndPostIdAndReplyIdIsNull(post.getId());
            longList += reactSize;
            return longList;
        } else {
            return longList;
        }
    }


    public Long getReactTotalSizeForOneTrendyPostsWithinOneYear() {
        Long longList = 0L;
        Post post = getOneTrendyPostForLoginUserWithinOneYear();
        if (post != null) {
            Long reactSize = reactService.finReactByCommentIdIsNullAndPostIdAndReplyIdIsNull(post.getId());
            longList += reactSize;
            return longList;
        } else {
            return longList;
        }
    }

    public Long getCommentTotalSizeForOneTrendyPostsWithinOneMonth() {
        Long longList = 0L;
        Post post = getOneTrendyPostForLoginUserWithinOneMonth();
        if (post != null) {
            List<Comment> commentCount = new ArrayList<>();
            Long comment = commentService.findCommentSizeByPostId(post.getId());
            List<Comment> commentList = commentService.findCommentsByPostId(post.getId());
            commentCount.addAll(commentList);
            longList += comment;
            for (Comment comment1 : commentCount) {
                Long reply = replyService.findReplySizeByCommentId(comment1.getId());
                longList += reply;
            }
            return longList;
        } else {
            return longList;
        }
    }

    public Long getCommentTotalSizeForOneTrendyPostsWithinOneYear() {
        Long longList = 0L;
        Post post = getOneTrendyPostForLoginUserWithinOneYear();
        if (post != null) {
            List<Comment> commentCount = new ArrayList<>();
            Long comment = commentService.findCommentSizeByPostId(post.getId());
            List<Comment> commentList = commentService.findCommentsByPostId(post.getId());
            commentCount.addAll(commentList);
            longList += comment;
            for (Comment comment1 : commentCount) {
                Long reply = replyService.findReplySizeByCommentId(comment1.getId());
                longList += reply;
            }
            return longList;
        } else {
            return longList;
        }
    }

    public Post getOneTrendyPostForLoginUserWithinOneMonth() {
        List<Post> postList = getTrendyPostForLoginUserInOneMonth();
        if (!postList.isEmpty()) {
            Post mostTrendyPost = null;
            Long maxTotalSize = 0L;
            for (Post post : postList) {
                Long reactSize = reactService.finReactByCommentIdIsNullAndPostIdAndReplyIdIsNull(post.getId());
                Long commentSize = commentService.findCommentSizeByPostId(post.getId());
                List<Comment> comments = commentService.findCommentsByPostId(post.getId());
                Long replySize = 0L;
                for (Comment comment : comments) {
                    replySize += replyService.findReplySizeByCommentId(comment.getId());
                }
                Long totalSize = reactSize + commentSize + replySize;
                if (totalSize > maxTotalSize) {
                    maxTotalSize = totalSize;
                    mostTrendyPost = post;
                }
            }
            return mostTrendyPost;
        } else {
            return null;
        }
    }


    public Post getOneTrendyPostForLoginUserWithinOneYear() {
        List<Post> postList = getTrendyPostForLoginUserInOneYear();
        if (!postList.isEmpty()) {
            Post mostTrendyPost = null;
            Long maxTotalSize = 0L;
            for (Post post : postList) {
                Long reactSize = reactService.finReactByCommentIdIsNullAndPostIdAndReplyIdIsNull(post.getId());
                Long commentSize = commentService.findCommentSizeByPostId(post.getId());
                List<Comment> comments = commentService.findCommentsByPostId(post.getId());
                Long replySize = 0L;
                for (Comment comment : comments) {
                    replySize += replyService.findReplySizeByCommentId(comment.getId());
                }
                Long totalSize = reactSize + commentSize + replySize;
                if (totalSize > maxTotalSize) {
                    maxTotalSize = totalSize;
                    mostTrendyPost = post;
                }
            }
            return mostTrendyPost;
        } else {
            return null;
        }
    }
// for login user trendy post end

    //for trendy group start
    public List<Post> getTrendyPostsForGroupWithinOneYear() {
        List<Post> postList = getActivePostByMostCommunityMembers();
        if (!postList.isEmpty()) {
            LocalDate currentDate = LocalDate.now();
            LocalDate startDateOfYear = LocalDate.of(currentDate.getYear(), 1, 1);
            return postList.stream()
                    .filter(post -> {
                        Date postDate = post.getCreatedDate();
                        LocalDate localDate = postDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        return !localDate.isBefore(startDateOfYear);
                    })
                    .toList();
        } else {
            return null;
        }
    }

    public List<Post> getTrendyPostsForGroupWithinOneMonth() {
        List<Post> postList = getActivePostByMostCommunityMembers();
        if (!postList.isEmpty()) {
            LocalDate currentDate = LocalDate.now();
            LocalDate startDateOfMonth = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), 1);
            return postList.stream()
                    .filter(post -> {
                        Date postDate = post.getCreatedDate();
                        LocalDate localDate = postDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        return localDate.isAfter(startDateOfMonth);
                    })
                    .toList();
        } else {
            return null;
        }
    }


    public Long getReactSizeForMostCommunityMembersWithinOneMonth() {
        Long longList = 0L;
        List<Post> postList = getTrendyPostsForGroupWithinOneMonth();
        if (!postList.isEmpty()) {
            for (Post post : postList) {
                Long reactSize = reactService.finReactByCommentIdIsNullAndPostIdAndReplyIdIsNull(post.getId());
                longList += reactSize;
            }
            return longList;
        }
        return longList;
    }


    public Long getReactSizeForMostCommunityMembersWithinOneYear() {
        Long longList = 0L;
        List<Post> postList = getTrendyPostsForGroupWithinOneYear();
        if (!postList.isEmpty()) {
            for (Post post : postList) {
                Long reactSize = reactService.finReactByCommentIdIsNullAndPostIdAndReplyIdIsNull(post.getId());
                longList += reactSize;
            }
            return longList;
        }
        return longList;
    }


    public Long getReactSizeForMostCommunityMembers() {
        Long longList = 0L;
        List<Post> postList = getActivePostByMostCommunityMembers();
        if (!postList.isEmpty()) {
            for (Post post : postList) {
                Long reactSize = reactService.finReactByCommentIdIsNullAndPostIdAndReplyIdIsNull(post.getId());
                longList += reactSize;
            }
            return longList;
        }
        return longList;
    }


    public Long getCommentSizeForMostCommunityMembersWithinOneMonth() {
        Long longList = 0L;
        List<Post> postList = getTrendyPostsForGroupWithinOneMonth();
        if (!postList.isEmpty()) {
            for (Post post : postList) {
                Long commentSize = commentService.findCommentSizeByPostId(post.getId());
                List<Comment> comments = commentService.findCommentsByPostId(post.getId());
                Long replySize = 0L;
                for (Comment comment : comments) {
                    replySize += replyService.findReplySizeByCommentId(comment.getId());
                }
                longList = commentSize + replySize;
            }
            return longList;
        } else {
            return longList;
        }
    }

    public Long getCommentSizeForMostCommunityMembersWithinOneYear() {
        Long longList = 0L;
        List<Post> postList = getTrendyPostsForGroupWithinOneYear();
        if (!postList.isEmpty()) {
            for (Post post : postList) {
                Long commentSize = commentService.findCommentSizeByPostId(post.getId());
                List<Comment> comments = commentService.findCommentsByPostId(post.getId());
                Long replySize = 0L;
                for (Comment comment : comments) {
                    replySize += replyService.findReplySizeByCommentId(comment.getId());
                }
                longList = commentSize + replySize;
            }
            return longList;
        } else {
            return longList;
        }
    }


    public Long getCommentSizeForMostCommunityMembers() {
        Long longList = 0L;
        List<Post> postList = getActivePostByMostCommunityMembers();
        if (!postList.isEmpty()) {
            for (Post post : postList) {
                Long commentSize = commentService.findCommentSizeByPostId(post.getId());
                List<Comment> comments = commentService.findCommentsByPostId(post.getId());
                Long replySize = 0L;
                for (Comment comment : comments) {
                    replySize += replyService.findReplySizeByCommentId(comment.getId());
                }
                longList = commentSize + replySize;
            }
            return longList;
        } else {
            return longList;
        }
    }


    public List<Post> getActivePostByMostCommunityMembers() {
        var community = getCommunityByMostMemberList();
        List<Post> posts = new ArrayList<>();
        if (community != null) {
            List<User_Group> user_groups = user_groupService.findByCommunityId(community.getId());
            for (User_Group user_group : user_groups) {
                List<Post> postList = postService.findAllPostByIsDeletedAndUserGroupId(user_group.getId());
                posts.addAll(postList);
            }
            return posts;
        } else {
            return null;
        }
    }

    public Community getCommunityByMostMemberList() {
        List<Community> communityList = communityService.findAllByIsActive();
        if (!communityList.isEmpty() && communityList != null) {
            Community mostTrendyCommunity = null;
            long memberSize = 0L;
            for (Community community : communityList) {
                List<User_Group> user_groups = user_groupService.findByCommunityId(community.getId());
                long userGroupSize = user_groups.size();
                if (userGroupSize > memberSize) {
                    memberSize = userGroupSize;
                    mostTrendyCommunity = community;
                }
            }
            return mostTrendyCommunity;
        } else {
            return null;
        }
    }


     public List<User_Group> getAllUserGroupWithCommunityId(){
        Community community = getCommunityByMostMemberList();
        List<User_Group> user_groupList = new ArrayList<>();
        List<User_Group> user_groups = user_groupService.findByCommunityId(community.getId());
         if(!user_groups.isEmpty()){
             for(User_Group user_group:user_groups){
                 if(!user_group.getUser().getId().equals(999)){
                     user_groupList.add(user_group);
                 }
             }
             return user_groupList;
         }else{
             return user_groupList;
         }
     }

    //for trendy group end
}
