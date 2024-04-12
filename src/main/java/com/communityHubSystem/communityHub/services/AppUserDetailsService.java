package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String staff_id) throws UsernameNotFoundException {
        return userRepository.findByStaffId(staff_id)
                .map(user ->
                        User.withUsername(staff_id)
                .password(user.getPassword())
                .disabled(!user.isActive())
                .accountExpired(!user.isActive())
                .authorities(AuthorityUtils.createAuthorityList(user.getRole().name()))
                .build())
                .orElseThrow(() -> new UsernameNotFoundException("There is no user with that staff id"));

    }

}
