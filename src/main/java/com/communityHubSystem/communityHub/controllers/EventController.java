package com.communityHubSystem.communityHub.controllers;


import com.communityHubSystem.communityHub.dto.EventDTO;
import com.communityHubSystem.communityHub.dto.PollDto;
import com.communityHubSystem.communityHub.models.Event;
import com.communityHubSystem.communityHub.models.Poll;
import com.communityHubSystem.communityHub.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("event")
public class EventController {

    private final EventService eventService;


    @PostMapping("/createEvent")
    @ResponseBody
    public ResponseEntity<Event> createEvent(@ModelAttribute EventDTO eventDTO) throws ParseException, IOException {
        return ResponseEntity.ok(eventService.createEvent(eventDTO));
    }

    @PostMapping("/makeEvent")
    @ResponseBody
    public ResponseEntity<Event> makeEvent(@RequestBody EventDTO eventDTO) throws ParseException, IOException {
        return ResponseEntity.ok(eventService.createEvent(eventDTO));
    }

    @PutMapping("/updateEvent")
    @ResponseBody
    public ResponseEntity<Event> editEvent(@RequestBody EventDTO eventDTO) {
        return ResponseEntity.ok(eventService.editEvent(eventDTO));
    }

    @DeleteMapping("/deleteEvent/{id}")
    @ResponseBody
    public ResponseEntity<Event> deleteEvent(@PathVariable("id") String id) {
        return ResponseEntity.ok(eventService.deleteEvent(Long.valueOf(id)));
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Event>> findAllEvents() {
        System.err.println(eventService.getAllEvents());
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllEvents());
    }

    @GetMapping("/recent")
    @ResponseBody
    public ResponseEntity<List<Event>> findRecentEvents() {
        return ResponseEntity.ok(eventService.getEventOfRecentDays());
    }

    @GetMapping("/searchEvent/{input}")
    @ResponseBody
    public ResponseEntity<List<Event>> searchEvents(@PathVariable("input") String input) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.filterDeletedEvents(eventService.searchMethodForEvent(input)));
    }

    @GetMapping("/searchPolls/{input}")
    @ResponseBody
    public ResponseEntity<List<Event>> searchPolls(@PathVariable("input") String input) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.searchMethodForPoll(input));
    }

    @GetMapping("/getVoteEvents")
    @ResponseBody
    public ResponseEntity<List<Event>> findAllVoteEvents() {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllVoteEvents());
    }

    @PostMapping("/giveVote")
    @ResponseBody
    public ResponseEntity<?> giveVote(@RequestBody PollDto pollDto) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.giveVote(pollDto));
    }

    @GetMapping("/refresh")
    @ResponseBody
    public ResponseEntity<?> refresh() {
        eventService.refresh();
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

    @GetMapping("/getAllPolls/{page}")
    @ResponseBody
    public ResponseEntity<List<Object>> getAllPolls(@PathVariable("page") String page) {
        return ResponseEntity.ok(eventService.getAllPolls(page));
    }

    @GetMapping("/deleteVote/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteVote(@PathVariable("id") String id) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.deleteVoteOfAPerson(Long.valueOf(id)));
    }

    @GetMapping("/getEventById/{id}")
    @ResponseBody
    public ResponseEntity<Event> getEventById(@PathVariable("id") String id) {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getEventById(Long.parseLong(id)));
    }

    @PostMapping("/eventUpdate")
    @ResponseBody
    public ResponseEntity<Event> updateEvent(@RequestParam(value = "eventId", required = false) String eventId,
                                             @RequestParam(value = "title", required = false) String title,
                                             @RequestParam(value = "description", required = false) String description,
                                             @RequestParam(value = "startDate", required = false) String startDate,
                                             @RequestParam(value = "endDate", required = false) String endDate,
                                             @RequestParam(value = "location", required = false) String location,
                                             @RequestParam(value = "photo", required = false) String photo,
                                             @RequestParam(value = "newPhoto", required = false) MultipartFile newPhoto) throws IOException {
        var eventDto = eventService.makeEventDto(eventId, title, description, startDate, endDate, location, photo, newPhoto);
        return ResponseEntity.ok(eventService.updateEventPost(eventDto));
    }

    @DeleteMapping("/deleteAEvent/{id}")
    @ResponseBody
    public ResponseEntity<Event> deleteAEvent(@PathVariable("id") String id) {
        return ResponseEntity.ok(eventService.deleteAEvent(Long.parseLong(id)));
    }

    @PostMapping("/createAVotePost")
    @ResponseBody
    public ResponseEntity<Event> createAVotePost(@ModelAttribute EventDTO eventDTO,
                                                 @RequestParam("opts") List<String> opts,
                                                 @RequestParam(value = "multipartFile", required = false) MultipartFile multipartFile) throws IOException, ParseException {
        System.err.println(eventDTO);
        return ResponseEntity.status(HttpStatus.OK).body(eventService.createAPoll(eventDTO, opts, multipartFile));
    }

    @GetMapping("/getAllPollPost")
    @ResponseBody
    public ResponseEntity<List<Event>> getAllPollPost() {
        return ResponseEntity.status(HttpStatus.OK).body(eventService.getAllPollPost());
    }

    @GetMapping("/giveVote/{voteOptionId}/{eventId}")
    @ResponseBody
    public ResponseEntity<Event> giveVote(@PathVariable("voteOptionId") String voteOptionId,
                                          @PathVariable("eventId") String eventId) {
        return ResponseEntity.ok(eventService.giveVote(Long.parseLong(voteOptionId), Long.parseLong(eventId)));
    }

    @GetMapping("/getCount/{voteOptionId}")
    @ResponseBody
    public ResponseEntity<String> getCount(@PathVariable("voteOptionId") String voteOptionId) {
        return ResponseEntity.ok(eventService.getCountOfTheVoteOption(Long.parseLong(voteOptionId)));
    }

    @GetMapping("/checkVoted/{eventId}")
    @ResponseBody
    public ResponseEntity<Boolean> checkVoted(@PathVariable("eventId") String eventId) {
        return ResponseEntity.ok(eventService.checkIfUserIsAlreadyVoted(Long.valueOf(eventId)));
    }

    @GetMapping("/deleteAPoll/{eventId}")
    @ResponseBody
    public ResponseEntity<Poll> deleteAPoll(@PathVariable("eventId") String eventId) {
        return ResponseEntity.ok(eventService.deleteAPoll(Long.parseLong(eventId)));
    }

    @PostMapping("/pollEventUpdate")
    @ResponseBody
    public ResponseEntity<Event> updatePollEvent(@RequestParam(value = "eventId", required = false) String eventId,
                                                 @RequestParam(value = "title", required = false) String title,
                                                 @RequestParam(value = "description", required = false) String description,
                                                 @RequestParam(value = "startDate", required = false) String startDate,
                                                 @RequestParam(value = "endDate", required = false) String endDate,
                                                 @RequestParam(value = "location", required = false) String location,
                                                 @RequestParam(value = "photo", required = false) String photo,
                                                 @RequestParam(value = "newPhoto", required = false) MultipartFile newPhoto,
                                                 @RequestParam(value = "oldOpts", required = false) List<String> oldOpts,
                                                 @RequestParam(value = "newOpts", required = false) List<String> newOpts) throws IOException, IOException {
        var eventDto = eventService.makeEventDto(eventId, title, description, startDate, endDate, location, photo, newPhoto);

        eventService.updatePollOptions(oldOpts, newOpts, Long.valueOf(eventId));
        return ResponseEntity.ok(eventService.updateEventPost(eventDto));
    }

    @GetMapping("/checkVotedMark/{voteOptionId}")
    @ResponseBody
    public ResponseEntity<Boolean> checkVotedMark(@PathVariable("voteOptionId") String voteOptionId) {
        return ResponseEntity.ok(eventService.checkVotedMark(Long.valueOf(voteOptionId)));
    }

    @GetMapping("/getEventsForNewsfeed/{page}")
    @ResponseBody
    public ResponseEntity<List<Event>> sendEvents(@PathVariable("page")String page){
        return ResponseEntity.ok(eventService.getEventsForNewsfeed(page).getContent());
    }
}
