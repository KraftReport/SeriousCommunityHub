package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.dto.EventDTO;
import com.communityHubSystem.communityHub.dto.PollDto;
import com.communityHubSystem.communityHub.models.Event;
import com.communityHubSystem.communityHub.models.Poll;

import java.text.ParseException;
import java.util.List;

public interface EventService {
    public Event createEvent(EventDTO eventDTO) throws ParseException;
    public List<Event> getAllEvents();
    public Poll giveYES(PollDto pollDto);
    public Poll giveNO(PollDto pollDto);
    public List<Event> getAllVoteEvents();
    public Poll giveVote(PollDto pollDto);

}
