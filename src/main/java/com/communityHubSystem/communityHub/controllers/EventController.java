package com.communityHubSystem.communityHub.controllers;


import com.communityHubSystem.communityHub.dto.EventDTO;
import com.communityHubSystem.communityHub.dto.PollDto;
import com.communityHubSystem.communityHub.models.Event;
import com.communityHubSystem.communityHub.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/event")
public class EventController {
    private final EventService eventService;

    @PostMapping("/createEvent")
    @ResponseBody
    public ResponseEntity<Event> createEvent(@ModelAttribute EventDTO eventDTO) throws ParseException {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.createEvent(eventDTO));
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Event>> findAllEvents(){
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllEvents());
    }

    @GetMapping("/getVoteEvents")
    @ResponseBody
    public ResponseEntity<List<Event>> findAllVoteEvents(){
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllVoteEvents());
    }

    @PostMapping("/giveVote")
    @ResponseBody
    public ResponseEntity<?> giveVote(@ModelAttribute PollDto pollDto){
        return ResponseEntity.status(HttpStatus.OK).body(eventService.giveVote(pollDto));
    }

}
