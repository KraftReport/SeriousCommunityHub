package com.communityHubSystem.communityHub.configs;

import com.communityHubSystem.communityHub.models.UserAccessLog;
import com.communityHubSystem.communityHub.repositories.UserAccessLogRepository;
import com.communityHubSystem.communityHub.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppUserCheckListener {

    private final UserService userService;
    private final UserAccessLogRepository userAccessLogRepository;

    @EventListener
    void onSuccess(AuthenticationSuccessEvent event) {
        var time = LocalDateTime.ofInstant(new Date(event.getTimestamp()).toInstant(),
                ZoneId.systemDefault());
        var staffId = event.getAuthentication().getName();
        var user = userService.findByStaffId(staffId).orElseThrow();
        log.info("{} is sign in at {}", user.getEmail(), time);
        userAccessLogRepository.save(new UserAccessLog(user.getEmail(), UserAccessLog.LoginType.SIGN_IN, time));
    }


    @EventListener
    void onFailure(AbstractAuthenticationFailureEvent event) {
        var time = LocalDateTime.ofInstant(new Date(event.getTimestamp()).toInstant(),
                ZoneId.systemDefault());
        var staffId = event.getAuthentication().getName();
        var user = userService.findByStaffId(staffId).orElseThrow();
        log.info("{} is fail to sign in at  because of {}", user.getStaffId(), time, event.getException().getMessage());
        userAccessLogRepository.save(new UserAccessLog(user.getEmail(), event.getException().getMessage(), UserAccessLog.LoginType.ERROR, time));
    }


    @EventListener
    void onSessionDestroyed(HttpSessionDestroyedEvent event) {
        event.getSecurityContexts().stream().findAny()
                .ifPresent(auth -> {
                    var time = LocalDateTime.ofInstant(new Date(event.getTimestamp()).toInstant(), ZoneId.systemDefault());
                    var staffId = auth.getAuthentication().getName();
                    var user = userService.findByStaffId(staffId).orElseThrow();
                    log.info("{} is sign out at {}", user.getEmail(), time);
                    userAccessLogRepository.save(new UserAccessLog(user.getEmail(), UserAccessLog.LoginType.SIGN_OUT, time));
                });
    }
}
