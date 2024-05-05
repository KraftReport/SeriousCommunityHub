package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.dto.EventDTO;
import com.communityHubSystem.communityHub.dto.PollDto;
import com.communityHubSystem.communityHub.models.Event;
import com.communityHubSystem.communityHub.models.Poll;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface EventService {
    public Event createEvent(EventDTO eventDTO) throws ParseException, IOException;
    public List<Event> getAllEvents();
    public Poll giveYES(PollDto pollDto);
    public Poll giveNO(PollDto pollDto);
    public List<Event> getAllVoteEvents();
    public Poll giveVote(PollDto pollDto);
    public Event editEvent(EventDTO eventDTO);
    public Event deleteEvent(Long id);
    public List<Event> searchMethodForEvent(String input);
    public List<Event> searchMethodForPoll(String input);
    public Event editPollPost(EventDTO eventDTO);
    public List<Event> getEventOfRecentDays();
    public void refresh();
    public List<Object> getAllPolls(String page);
    public String deleteVoteOfAPerson(Long id);
    public Event getEventById(Long id);

    public EventDTO makeEventDto(String eventId, String title, String description, String startDate, String endDate, String location, String photo, MultipartFile newPhoto);

    public Event updateEventPost(EventDTO eventDto) throws IOException;
    public List<Event> filterDeletedEvents(List<Event> events);
    public Event deleteAEvent(Long id);

    public Event createAPoll(EventDTO eventDTO, List<String> opts, MultipartFile photo) throws IOException, ParseException;
    public List<Event> getAllPollPost();
    public Event giveVote(Long voteOptionId,Long EventId);
    public String getCountOfTheVoteOption(Long voteOptionId);
    public Boolean checkIfUserIsAlreadyVoted(Long eventId);
    public Poll deleteAPoll(Long eventId);
    @Transactional
    public void updatePollOptions(List<String> oldOpts, List<String> newOpts, Long eventId);
    public boolean checkVotedMark(Long voteOptionId);
}
