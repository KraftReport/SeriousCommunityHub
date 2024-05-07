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

public interface PostRepository extends JpaRepository<Post,Long>, JpaSpecificationExecutor<Post> {

    @Query("SELECT p FROM Post p JOIN FETCH p.resources ORDER BY p.createdDate DESC")
    List<Post> findAllWithResources();

    List<Post> findPostsByUserId(Long id);

    List<Post> findPostsByAccess(Access access);

   List<Post> findAllByUserIdAndUserGroupId(Long userId, Long userGroupId);


    List<Post> findPostsByAccessOrderByCreatedDateDesc(Access access);

    List<Post> findAllByUserIdAndUserGroupIdOrderByCreatedDateDesc(Long userId, Long userGroupId);
   // Page<Post> findAllByUserIdAndUserGroupId(Long userId, Long userGroupId, Pageable pageable);

//    @Query("SELECT p FROM Post p JOIN FETCH p.resources WHERE p.access = :access ORDER BY p.createdDate DESC")
//    List<Post> findPostsByAccessWithPagination(@Param("access") Access access, Pageable pageable);
//
//    @Query("SELECT p FROM Post p JOIN FETCH p.resources WHERE p.user.id = :userId AND p.getUserGroup.id = :userGroupId ORDER BY p.createdDate DESC")
//    List<Post> findPostsByUserIdAndUserGroupIdWithPagination(@Param("userId") Long userId, @Param("userGroupId") Long userGroupId, Pageable pageable);

}
