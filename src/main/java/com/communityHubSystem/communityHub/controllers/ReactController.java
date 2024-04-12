package com.communityHubSystem.communityHub.controllers;

import com.communityHubSystem.communityHub.services.ReactService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ReactController {

    private final ReactService reactService;
    private final SimpMessagingTemplate messagingTemplate;
}
