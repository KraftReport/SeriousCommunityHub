package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.dto.EventDTO;
import com.communityHubSystem.communityHub.dto.PollDto;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.*;
import com.communityHubSystem.communityHub.repositories.*;
import com.communityHubSystem.communityHub.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final PollRepository pollRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final User_GroupRepository user_groupRepository;
    SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd");
    @Override
    public Event createEvent(EventDTO eventDTO) throws ParseException {
        if(eventDTO.getEventType().equals("EVENT")){
            return createEventPost(eventDTO);
        }else{
            return createPollPost(eventDTO);
        }
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findByEventType(Event.EventType.EVENT);
    }

    @Override
    public Poll giveYES(PollDto pollDto) {
        var poll = new Poll();
        poll.setType(Poll.Vote.YES);
        poll.setEvent(findByEventId(pollDto));
        poll.setDate(new Date());
        poll.setUser(getLoginUser());
        return pollRepository.save(poll);
    }

    @Override
    public Poll giveNO(PollDto pollDto) {
        var poll = new Poll();
        poll.setType(Poll.Vote.NO);
        poll.setEvent(findByEventId(pollDto));
        poll.setDate(new Date());
        poll.setUser(getLoginUser());
        return pollRepository.save(poll);
    }

    @Override
    public List<Event> getAllVoteEvents() {
        return eventRepository.findByEventType(Event.EventType.VOTE);
    }

    @Override
    public Poll giveVote(PollDto pollDto) {
        if(pollDto.getVoteType().equals("YES")) {
            return giveYES(pollDto);
        }else {
            return giveNO(pollDto);
        }
    }


    private Event findByEventId(PollDto pollDto){
        return eventRepository.findById(Long.valueOf(pollDto.getEventId())).orElseThrow(()->new CommunityHubException("event not found"));
    }

    private Event createEventPost(EventDTO eventDTO) throws ParseException {
        var event = setEventUp(eventDTO);
        return eventRepository.save(event);
    }

    private Event createPollPost(EventDTO eventDTO) throws ParseException {
        var event = setEventUp(eventDTO);
        event.setEventType(Event.EventType.VOTE);
        return eventRepository.save(event);
    }

    private Event setEventUp(EventDTO eventDTO) throws ParseException {
        var event = new Event();
        event.setAccess(checkAccess(eventDTO));
        event.setCreated_date(new Date());
        event.setDescription(eventDTO.getDescription());
        event.setStart_date(formatter.parse(eventDTO.getStart_date()));
        event.setEnd_date(formatter.parse(eventDTO.getEnd_date()));
        event.setUser(getLoginUser());
        event.setUser_group(setGroup(eventDTO));
        return eventRepository.save(event);
    }

    private Access checkAccess(EventDTO eventDTO){
        if(eventDTO.getGroupId()!=null){
            return  Access.PRIVATE;
        }else {
            return Access.PUBLIC;
        }
    }


    public User_Group setGroup(EventDTO eventDTO){
        if(checkAccess(eventDTO).equals(Access.PRIVATE)){
            var group = new User_Group();
            group.setCommunity(communityRepository.findById(Long.valueOf(eventDTO.getGroupId())).orElseThrow(()->new CommunityHubException("group not found")));
            group.setDate(new Date());
            group.setUser(getLoginUser());
            return user_groupRepository.save(group);
        }else {
            return null;
        }
    }



    private User getLoginUser(){
        return userRepository.findByStaffId(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new CommunityHubException("user not found"));
    }

}
