package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.Access;
import com.communityHubSystem.communityHub.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    @Query("SELECT p FROM Post p JOIN FETCH p.resources ORDER BY p.createdDate DESC")
    List<Post> findAllWithResources();

    List<Post> findPostsByUserId(Long id);

    List<Post> findPostsByAccess(Access access);

    List<Post> findAllByUserIdAndUserGroupId(Long userId, Long userGroupId);


    List<Post> findPostsByAccessOrderByCreatedDateDesc(Access access);

    //    List<Post> findAllByUserIdAndUserGroupIdOrderByCreatedDateDesc(Long userId, Long userGroupId);
    @Query(value = "SELECT * FROM post WHERE user_id = :userId AND user_group_id = :userGroupId ORDER BY created_date DESC", nativeQuery = true)
    List<Post> findAllByUserIdAndUserGroupIdByCreatedDate(Long userId, Long userGroupId);


    @Query(value = "select * from post where user_group_id = :id order by created_date desc", nativeQuery = true)
    List<Post> findAllByUserGroupId(Long id);

    List<Post> findAllPostsByIsDeletedAndUserId(boolean value, Long id);

    @Query("SELECT p FROM Post p WHERE p.isDeleted = :isDeleted AND p.userGroup.id = :userGroupId")
    List<Post> findAllByIsDeletedAndUserGroupIdJPQL(@Param("isDeleted") boolean isDeleted, @Param("userGroupId") Long userGroupId);

    Post findByUrl(String url);
    // Page<Post> findAllByUserIdAndUserGroupId(Long userId, Long userGroupId, Pageable pageable);

    //    @Query("SELECT p FROM Post p JOIN FETCH p.resources WHERE p.access = :access ORDER BY p.createdDate DESC")
//    List<Post> findPostsByAccessWithPagination(@Param("access") Access access, Pageable pageable);
//
//    @Query("SELECT p FROM Post p JOIN FETCH p.resources WHERE p.user.id = :userId AND p.getUserGroup.id = :userGroupId ORDER BY p.createdDate DESC")
//    List<Post> findPostsByUserIdAndUserGroupIdWithPagination(@Param("userId") Long userId, @Param("userGroupId") Long userGroupId, Pageable pageable);
    @Query("SELECT p FROM Post p WHERE YEAR(p.createdDate) = :year AND MONTH(p.createdDate) = :month")
    List<Post> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT p FROM Post p WHERE p.user.id = :userId AND YEAR(p.createdDate) = :year AND MONTH(p.createdDate) = :month")
    List<Post> findByUserIdAndYearAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

}
