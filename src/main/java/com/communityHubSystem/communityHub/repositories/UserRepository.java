package com.communityHubSystem.communityHub.repositories;

import com.communityHubSystem.communityHub.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByStaffId(String staffId);

    boolean existsByStaffId(String staffId);


    User findByName(String name);
}
