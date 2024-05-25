package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.*;
import com.communityHubSystem.communityHub.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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
    public ResponseEntity<Long> getOneTrendyPostForReactCountWithinOneMonth() {
        return ResponseEntity.status(HttpStatus.OK).body(getReactTotalSizeForOneTrendyPostsWithinOneMonth());
    }

    @GetMapping("/record-user-trendyReact-post-countWithOneYear")
    public ResponseEntity<Long> getOneTrendyPostForReactCountWithinOneYear() {
        return ResponseEntity.status(HttpStatus.OK).body(getReactTotalSizeForOneTrendyPostsWithinOneYear());
    }

    @GetMapping("/record-user-trendyComment-post-countWithOneMonth")
    public ResponseEntity<Long> getOneTrendyPostForCommentSizeWithinOneMonth() {
        return ResponseEntity.status(HttpStatus.OK).body(getCommentTotalSizeForOneTrendyPostsWithinOneMonth());
    }

    @GetMapping("/record-user-trendyComment-post-countWithOneYear")
    public ResponseEntity<Long> getOneTrendyPostForCommentSizeWithinOneYear() {
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
    public ResponseEntity<List<User_Group>> getTrendyCommunityMemberList() {
        return ResponseEntity.status(HttpStatus.OK).body(getAllUserGroupWithCommunityId());
    }


    @GetMapping("/record-groupPostReacts-listForOneMonth")
    public ResponseEntity<Long> getTotalReactsForTrendyCommunityWithinOneMonth() {
        return ResponseEntity.status(HttpStatus.OK).body(getReactSizeForMostCommunityMembersWithinOneMonth());
    }

    @GetMapping("/record-groupPostReacts-listForOneYear")
    public ResponseEntity<Long> getTotalReactsForTrendyCommunityWithinOneYear() {
        return ResponseEntity.status(HttpStatus.OK).body(getReactSizeForMostCommunityMembersWithinOneYear());
    }

    @GetMapping("/record-groupPostComments-listForOneMonth")
    public ResponseEntity<Long> getCommentTotalSizeForTrendyCommunityWithinOneMonth() {
        return ResponseEntity.status(HttpStatus.OK).body(getCommentSizeForMostCommunityMembersWithinOneMonth());
    }

    @GetMapping("/record-groupPostComments-listForOneYear")
    public ResponseEntity<Long> getCommentTotalSizeForTrendyCommunityWithinOneYear() {
        return ResponseEntity.status(HttpStatus.OK).body(getCommentSizeForMostCommunityMembersWithinOneYear());
    }

    // for admin view start

    //call active all users without admin
    @GetMapping("/activeUsers-forAdmin")
    public ResponseEntity<List<User>> getActiveUsersForAdmin() {
        return ResponseEntity.status(HttpStatus.OK).body(getAllActiveUserWithoutAdmin());
    }

    //get one post for admin in a month
    @GetMapping("/onlyOne-trendyPost-withinOneMonth")
    public ResponseEntity<Post> getOnlyOneTrendyPostForAdminInOneMonth() {
        return ResponseEntity.status(HttpStatus.OK).body(getOnlyOneTrendyPostWithinActiveUserInOneMonth());
    }

    //get one post for admin in a year
    @GetMapping("/onlyOne-trendyPost-withinOneYear")
    public ResponseEntity<Post> getOnlyOneTrendyPostForAdminInOneYear() {
        return ResponseEntity.status(HttpStatus.OK).body(getOnlyOneTrendyPostWithinActiveUserInOneYear());
    }

    //get unique post react
    @GetMapping("/onlyOne-trendyPostReacts-withinOneMonth/{id}")
    public ResponseEntity<Long> getOnlyOneTrendyPostReactsForAdmin(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(getReactsForUniquePost(id));
    }


    //get unique post comment
    @GetMapping("/onlyOne-trendyPostComments-withinOneMonth/{id}")
    public ResponseEntity<Long> getOnlyOneTrendyPostCommentsForAdmin(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(getCommentsForUniquePost(id));
    }

    // get one user all posts reacts

    // dr do call yin react a kone ya
    @GetMapping("/activeUser-ReactsCount/{id}")
    public ResponseEntity<Long> getReactsForActiveUser(@PathVariable("id") Long id) {
        Long reactCount = getAllReactsForActiveUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(reactCount != null ? reactCount : 0L);
    }

    // get one user all posts comments
    @GetMapping("/activeUser-CommentsCount/{id}")
    public ResponseEntity<Long> getCommentsForActiveUser(@PathVariable("id") Long id) {
        Long commentCount = getAllCommentsForActiveUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(commentCount != null ? commentCount : 0L);
    }


    //get one user all posts and count
    @GetMapping("/activeUser-PostsForAdmin/{id}")
    public ResponseEntity<List<Post>> getPostsForActiveUser(@PathVariable("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(getAllPostsForActiveUser(id));
    }

    @GetMapping("/check-role")
    public ResponseEntity<User> getUserToCheckWhetherAdminOrUser() {
        return ResponseEntity.status(HttpStatus.OK).body(loginUser());
    }

    //dr do call yin month post ya
    @GetMapping("/getPosts-eachUser/month/{id}")
    public ResponseEntity<List<Post>> getPostsForEachUserInAMonth(@PathVariable("id") Long id) {
        List<Post> postList = getPostsForEachUserWithinOneMonth(id);
        return ResponseEntity.ok(postList.isEmpty() ? new ArrayList<>() : postList);
    }

    @GetMapping("/getPosts-eachUser/year/{id}")
    public ResponseEntity<List<Post>> getPostsForEachUserInAYear(@PathVariable("id") Long id) {
        List<Post> postList = getPostsForEachUserWithinOneYear(id);
        return ResponseEntity.ok(postList.isEmpty() ? new ArrayList<>() : postList);
    }

    @GetMapping("/getPosts-eachUser/all/{id}")
    public ResponseEntity<List<Post>> getPostsForEachUser(@PathVariable("id") Long id) {
        List<Post> postList = getAllPostsForEachUser(id);
        return ResponseEntity.ok(postList.isEmpty() ? new ArrayList<>() : postList);
    }

    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getPostData() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName().trim();

        Optional<User> optionalUser = userService.findByStaffId(staffId);
        if (optionalUser.isEmpty()) {
            throw new CommunityHubException("User name not found Exception!");
        }
        User people = optionalUser.get();
        Map<String, Object> postData = new HashMap<>();

        if (User.Role.ADMIN.equals(people.getRole())) {
            List<User> users = getAllActiveUserWithoutAdmin();
            for (User user : users) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("name", user.getName());
                userData.put("staffId", user.getStaffId());
                userData.put("data", getUserData(user.getId()));
                postData.put(String.valueOf(user.getId()), userData);
            }
        }
        return new ResponseEntity<>(postData, HttpStatus.OK);
    }

    @GetMapping("/barchartdata")
    public ResponseEntity<Map<String, Object>> getBarChartData() {
        var staffId = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> optionalUser = userService.findByStaffId(staffId);
        if (optionalUser.isEmpty()) {
            throw new CommunityHubException("User name not found Exception!");
        }

        User people = optionalUser.get();
        Map<String, Object> postData = new HashMap<>();

        if (User.Role.USER.equals(people.getRole())) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", people.getName());
            userData.put("staffId", people.getStaffId());
            userData.put("role",people.getRole());
            userData.put("data", getUserData(people.getId()));
            postData.put(String.valueOf(people.getId()), userData);
        }

        return new ResponseEntity<>(postData, HttpStatus.OK);
    }

    @GetMapping("/dataPerMonth/{id}/{year}/{month}")
    public ResponseEntity<Map<String, Object>> getPostDataForEachMonth(@PathVariable("id")Long id,
                                                                       @PathVariable("year")int year,
                                                                       @PathVariable("month")int month) {
        var staffId = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> optionalUser = userService.findByStaffId(staffId);
        if (optionalUser.isEmpty()) {
            throw new CommunityHubException("User name not found Exception!");
        }

        User people = optionalUser.get();
        Map<String, Object> postData = new HashMap<>();

        if (User.Role.ADMIN.equals(people.getRole())) {
            List<User> users = getAllActiveUserWithoutAdmin();
            for (User user : users) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("name", user.getName());
                userData.put("staffId", user.getStaffId());
                userData.put("data", getUserDataForEachPost(id,year,month));
                postData.put(String.valueOf(user.getId()), userData);
            }
        } else {
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", people.getName());
            userData.put("staffId", people.getStaffId());
            userData.put("data", getUserData(people.getId()));
            postData.put(String.valueOf(people.getId()), userData);
        }

        return new ResponseEntity<>(postData, HttpStatus.OK);
    }


    @GetMapping("/posts/by-month/{year}")
    public ResponseEntity<Map<Integer, List<Post>>> getPostsByMonth(@PathVariable("year") int year) {
        Map<Integer, List<Post>> postsByMonth = getAllPostsPerMonth(year);
        return new ResponseEntity<>(postsByMonth, HttpStatus.OK);
    }

    @GetMapping("/posts-perMonth-forAdmin/{id}/{year}/{month}")
    public ResponseEntity<List<Post>> getAllPostsByMonthForAdmin(@PathVariable("id") Long id,
                                                                 @PathVariable("year") int year,
                                                                 @PathVariable("month") int month) {
        List<Post> postList = getAllPostsForEachMonth(id, year, month);
        if (!postList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(postList);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(Collections.EMPTY_LIST);
        }
    }

    @GetMapping("/posts-perMonth-reactSize-forAdmin/{id}/{year}/{month}")
    public ResponseEntity<Long> getAllPostsReactSizeByMonthForAdmin(@PathVariable("id") Long id,
                                                                    @PathVariable("year") int year,
                                                                    @PathVariable("month") int month) {
        Long reactCount = getReactSizeForEachMonth(id, year, month);
        return ResponseEntity.status(HttpStatus.OK).body(reactCount);

    }

    @GetMapping("/posts-perMonth-commentSize-forAdmin/{id}/{year}/{month}")
    public ResponseEntity<Long> getAllPostsCommentSizeByMonthForAdmin(@PathVariable("id") Long id,
                                                                      @PathVariable("year") int year,
                                                                      @PathVariable("month") int month) {
        Long commentCount = getCommentSizeForEachMonth(id, year, month);
        return ResponseEntity.status(HttpStatus.OK).body(commentCount);
    }


    @GetMapping("/postCountsPerMonth/{id}/{year}")
    public ResponseEntity<Map<String, Object>> getPostCountsPerMonth(@PathVariable("id") Long id,
                                                                     @PathVariable("year") int year) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String staffId = auth.getName().trim();

        Optional<User> optionalUser = userService.findByStaffId(staffId);
        if (optionalUser.isEmpty()) {
            throw new CommunityHubException("User name not found Exception!");
        }

        User people = optionalUser.get();
        Map<String, Object> postData = new HashMap<>();

        if (User.Role.ADMIN.equals(people.getRole())) {
            List<User> users = getAllActiveUserWithoutAdmin();
            for (User user : users) {
                Map<String, Object> userData = getUserDataForEachUserPost(id, year);
                userData.put("name", user.getName());
                userData.put("staffId", user.getStaffId());
                postData.put(String.valueOf(user.getId()), userData);
            }
        }
        return new ResponseEntity<>(postData, HttpStatus.OK);
    }


    private Map<String, Object> getUserDataForEachUserPost(Long userId, int year) {
        Map<String, Object> userData = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            Long postsWithinMonth = (long) getAllPostsForEachMonth(userId, year, month).size();
            userData.put(String.valueOf(month), postsWithinMonth);
        }
        return userData;
    }

    //for admin view end
    private Map<String, Object> getUserData(Long userId) {
        Map<String, Object> userData = new HashMap<>();
        Long postsWithinMonth = (long) getPostsForEachUserWithinOneMonth(userId).size();

        Long postsWithinYear = (long) getPostsForEachUserWithinOneYear(userId).size();
        Long totalPosts = (long) getAllPostsForEachUser(userId).size();
        Long totalReacts = getAllReactsForActiveUser(userId);

        Long totalComments = getAllCommentsForActiveUser(userId);

        userData.put("within_one_month", postsWithinMonth);
        userData.put("within_one_year", postsWithinYear);
        userData.put("total_posts", totalPosts);
        userData.put("total_reacts", totalReacts);
        userData.put("total_comments", totalComments);

        return userData;
    }

    private Map<String, Object> getUserDataForEachPost(Long userId,int year,int month) {
        Map<String, Object> userData = new HashMap<>();
        Long postsWithinMonth = (long) getAllPostsForEachMonth(userId,year,month).size();
        Long totalReacts = getReactSizeForEachMonth(userId,year,month);

        Long totalComments = getCommentSizeForEachMonth(userId,year,month);

        userData.put("within_one_month", postsWithinMonth);
        userData.put("total_reacts", totalReacts);
        userData.put("total_comments", totalComments);

        return userData;
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


    public List<User_Group> getAllUserGroupWithCommunityId() {
        Community community = getCommunityByMostMemberList();
        List<User_Group> user_groupList = new ArrayList<>();
        List<User_Group> user_groups = user_groupService.findByCommunityId(community.getId());
        if (!user_groups.isEmpty()) {
            for (User_Group user_group : user_groups) {
                var user = userService.findById(user_group.getUser().getId());
                if (!user.getRole().equals(User.Role.ADMIN)) {
                    user_groupList.add(user_group);
                }
            }
            return user_groupList;
        } else {
            return user_groupList;
        }
    }

    //for trendy group end

    //for all user posts start

//    public Post getOnlyOneTrendyPostWithinActiveUser() {
//        Post mostTrendyPost = null;
//        Long mostCount = 0L;
//        var users = getAllActiveUser();
//        List<Post> postList = new ArrayList<>();
//        for (User user : users) {
//            var posts = postService.findAllPostByIsDeleted(false, user.getId());
//            postList.addAll(posts);
//        }
//
//        for (Post post : postList) {
//            Long countList = 0L;
//            Long sizeOfReact = reactService.finReactByCommentIdIsNullAndPostIdAndReplyIdIsNull(post.getId());
//            countList += sizeOfReact;
//            Long sizeOfComment = commentService.findCommentSizeByPostId(post.getId());
//            countList += sizeOfComment;
//            List<Comment> commentList = commentService.findCommentsByPostId(post.getId());
//            for (Comment comment : commentList) {
//                Long sizeOfReply = replyService.findReplySizeByCommentId(comment.getId());
//                countList += sizeOfReply;
//            }
//            if (countList > mostCount) {
//                mostCount = countList;
//                mostTrendyPost = post;
//            }
//        }
//
//        return mostTrendyPost;
//    }

    public Post getOnlyOneTrendyPostWithinActiveUser() {
        var users = getAllActiveUser();
        List<Post> postList = users.stream()
                .flatMap(user -> postService.findAllPostByIsDeleted(false, user.getId()).stream())
                .collect(Collectors.toList());
        return postList.stream()
                .max(Comparator.comparingLong(post -> {
                    Long sizeOfReact = reactService.finReactByCommentIdIsNullAndPostIdAndReplyIdIsNull(post.getId());
                    Long sizeOfComment = commentService.findCommentSizeByPostId(post.getId());
                    Long sizeOfReply = commentService.findCommentsByPostId(post.getId()).stream()
                            .mapToLong(comment -> replyService.findReplySizeByCommentId(comment.getId()))
                            .sum();
                    return sizeOfReact + sizeOfComment + sizeOfReply;
                }))
                .orElse(null);
    }


    public Long getAllCommentsForActiveUser(Long userId) {
        var postList = getAllPostsForActiveUser(userId);
        if (!postList.isEmpty()) {
            Long commentSize = 0L;
            for (Post post : postList) {
                Long commentCount = commentService.findCommentSizeByPostId(post.getId());
                List<Comment> commentList = commentService.findCommentsByPostId(post.getId());
                commentSize += commentCount;
                for (Comment comment : commentList) {
                    Long replyCount = replyService.findReplySizeByCommentId(comment.getId());
                    commentSize += replyCount;
                }
            }
            return commentSize;
        } else {
            return null;
        }
    }

    public Long getAllReactsForActiveUser(Long userId) {
        var posts = getAllPostsForActiveUser(userId);
        if (!posts.isEmpty()) {
            Long reactCount = 0L;
            for (Post post : posts) {
                Long reactSize = reactService.finReactByCommentIdIsNullAndPostIdAndReplyIdIsNull(post.getId());
                reactCount += reactSize;
            }

            return reactCount;
        } else {
            return null;
        }
    }

    public Long getCommentsForUniquePost(Long postId) {
        var post = postService.findById(postId);
        Long count = 0L;
        var commentSize = commentService.findCommentSizeByPostId(post.getId());
        count += commentSize;
        List<Comment> commentList = commentService.findCommentsByPostId(post.getId());
        for (Comment comment : commentList) {
            count += replyService.findReplySizeByCommentId(comment.getId());
        }
        return count;
    }

    public Long getReactsForUniquePost(Long postId) {
        var post = postService.findById(postId);
        return reactService.finReactByCommentIdIsNullAndPostIdAndReplyIdIsNull(post.getId());
    }


    public List<Post> getAllPostsForActiveUser(Long userId) {
        return postService.findAllPostByIsDeleted(false, userId);
    }

    public List<User> getAllActiveUser() {
        return userService.getAllActiveUser();
    }

    public List<User> getAllActiveUserWithoutAdmin() {
        List<User> users = userService.getAllActiveUser();
        List<User> userList = new ArrayList<>();
        for (User user : users) {
            if (!user.getRole().equals(User.Role.ADMIN)) {
                userList.add(user);
            }
        }
        return userList;
    }
    //for all user posts end

    //for admin start

    public Post getOnlyOneTrendyPostWithinActiveUserInOneMonth() {
        LocalDate currentDate = LocalDate.now();
        LocalDate startDateOfMonth = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), 1);
        List<Post> trendyPosts = getTrendyPostForAdminInOneMonth(startDateOfMonth);
        return trendyPosts.stream()
                .max(Comparator.comparingLong(this::calculatePostEngagement))
                .orElse(null);
    }

    public Post getOnlyOneTrendyPostWithinActiveUserInOneYear() {
        LocalDate currentDate = LocalDate.now();
        LocalDate startDateOfYear = LocalDate.of(currentDate.getYear(), 1, 1);
        List<Post> trendyPosts = getTrendyPostForAdminInOneYear(startDateOfYear);
        return trendyPosts.stream()
                .max(Comparator.comparingLong(this::calculatePostEngagement))
                .orElse(null);
    }

    public List<Post> getTrendyPostForAdminInOneMonth(LocalDate startDateOfMonth) {
        List<Post> postList = getAllPostsForAdminToShowActiveUser();
        return postList.stream()
                .filter(post -> {
                    LocalDate postDate = post.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return !postDate.isBefore(startDateOfMonth);
                })
                .collect(Collectors.toList());
    }

    public List<Post> getTrendyPostForAdminInOneYear(LocalDate startDateOfYear) {
        List<Post> postList = getAllPostsForAdminToShowActiveUser();
        return postList.stream()
                .filter(post -> {
                    LocalDate postDate = post.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return !postDate.isBefore(startDateOfYear);
                })
                .collect(Collectors.toList());
    }

    public List<Post> getPostsForEachUserWithinOneMonth(Long userId) {
        var posts = postService.findAllPostByIsDeleted(false, userId);
        LocalDate currentDate = LocalDate.now();
        LocalDate startDateOfMonth = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), 1);
        return posts.stream()
                .filter(post -> {
                    LocalDate postDate = post.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return !postDate.isBefore(startDateOfMonth);
                })
                .collect(Collectors.toList());
    }

    public List<Post> getPostsForEachUserWithinOneYear(Long userId) {
        var posts = postService.findAllPostByIsDeleted(false, userId);
        LocalDate currentDate = LocalDate.now();
        LocalDate startDateOfYear = LocalDate.of(currentDate.getYear(), 1, 1);
        return posts.stream()
                .filter(post -> {
                    LocalDate postDate = post.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return !postDate.isBefore(startDateOfYear);
                })
                .collect(Collectors.toList());
    }

    public List<Post> getAllPostsForEachUser(Long userId) {
        return postService.findAllPostByIsDeleted(false, userId);
    }

    private long calculatePostEngagement(Post post) {
        Long sizeOfReact = reactService.finReactByCommentIdIsNullAndPostIdAndReplyIdIsNull(post.getId());
        Long sizeOfComment = commentService.findCommentSizeByPostId(post.getId());
        Long sizeOfReply = commentService.findCommentsByPostId(post.getId()).stream()
                .mapToLong(comment -> replyService.findReplySizeByCommentId(comment.getId()))
                .sum();
        return sizeOfReact + sizeOfComment + sizeOfReply;
    }

    public List<Post> getAllPostsForAdminToShowActiveUser() {
        var users = getAllActiveUser();
        List<User> userList = new ArrayList<>();
        for (User user : users) {
            if (!user.getRole().equals(User.Role.ADMIN)) {
                userList.add(user);
            }
        }
        List<Post> postList = userList.stream()
                .flatMap(user -> postService.findAllPostByIsDeleted(false, user.getId()).stream())
                .collect(Collectors.toList());
        return postList;
    }

    //for admin end
    public Map<Integer, List<Post>> getAllPostsPerMonth(int year) {
        Map<Integer, List<Post>> postsByMonth = new HashMap<>();

        for (int month = 1; month <= 12; month++) {
            List<Post> posts = postService.findByYearAndMonth(year, month);
            postsByMonth.put(month, posts);
        }
        return postsByMonth;
    }

    //over all
    public List<Post> getAllPostsForEachMonth(Long userId, int year, int month) {
        List<Post> postList = postService.findByUserIdAndYearAndMonth(userId, year, month);
        List<Post> posts = new ArrayList<>();

        for (Post post : postList) {
            if (!post.isDeleted()) {
                posts.add(post);
            }
        }
        return posts;
    }

    public Long getReactSizeForEachMonth(Long userId, int year, int month) {
        List<Post> postList = postService.findByUserIdAndYearAndMonth(userId, year, month);
        Long totalCount = 0L;
        if (!postList.isEmpty()) {
            for (Post post : postList) {
                Long reactSize = reactService.finReactByCommentIdIsNullAndPostIdAndReplyIdIsNull(post.getId());
                totalCount += reactSize;
            }
            return totalCount;
        } else {
            return totalCount;
        }
    }

    public Long getCommentSizeForEachMonth(Long userId, int year, int month) {
        Long totalCount = 0L;
        List<Post> postList = postService.findByUserIdAndYearAndMonth(userId, year, month);
        if (!postList.isEmpty()) {
            for (Post post : postList) {
                Long commentSize = commentService.findCommentSizeByPostId(post.getId());
                List<Comment> comments = commentService.findCommentsByPostId(post.getId());
                Long replySize = 0L;
                for (Comment comment : comments) {
                    replySize += replyService.findReplySizeByCommentId(comment.getId());
                }
                totalCount = commentSize + replySize;
            }
            return totalCount;
        } else {
            return totalCount;
        }
    }
}
