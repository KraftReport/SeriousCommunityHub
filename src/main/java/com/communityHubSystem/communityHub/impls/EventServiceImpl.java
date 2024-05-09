package com.communityHubSystem.communityHub.impls;

import com.cloudinary.Cloudinary;
import com.communityHubSystem.communityHub.dto.EventDTO;
import com.communityHubSystem.communityHub.dto.PollDto;
import com.communityHubSystem.communityHub.exception.CommunityHubException;
import com.communityHubSystem.communityHub.models.*;
import com.communityHubSystem.communityHub.repositories.*;
import com.communityHubSystem.communityHub.services.EventService;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final PollRepository pollRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final User_GroupRepository user_groupRepository;
    private final VoteOptionRepository voteOptionRepository;
    private final Cloudinary cloudinary;
    private final List<String> photoExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", "bmp", "tiff", "tif", "psv", "svg", "webp", "ico", "heic");

    List<Long> ids = new ArrayList<>();

    SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd");
    @Override
    public Event createEvent(EventDTO eventDTO) throws ParseException, IOException {
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

    @Override
    @Transactional
    public Event editEvent(EventDTO eventDTO) {
        System.err.println(eventDTO);
        var found = eventRepository.findById(Long.valueOf(eventDTO.getEventId())).orElseThrow(()->new CommunityHubException("event not found"));
        found.setDescription(eventDTO.getDescription());
        found.setStart_date(stringToDateChanger(eventDTO.getStart_date()));
        found.setEnd_date(stringToDateChanger(eventDTO.getEnd_date()));
        eventRepository.save(found);
        return found;
    }

    @Override
    @Transactional
    public Event deleteEvent(Long id) {
        eventRepository.deleteById(id);
        return new Event();
    }

    @Override
    public List<Event> searchMethodForEvent(String input) {
        var list = searchMethod(input);
        return list.stream().filter(s->s.getEventType()== Event.EventType.EVENT).collect(Collectors.toList());
    }

    @Override
    public List<Event> searchMethodForPoll(String input) {
        var list = searchMethod(input);
        return list.stream().filter(s->s.getEventType().equals(Event.EventType.VOTE)).collect(Collectors.toList());
    }

    @Override
    public Event editPollPost(EventDTO eventDTO) {
        return null;
    }

    @Override
    public List<Event> getEventOfRecentDays() {
        var calendar = Calendar.getInstance();
        var end = new Date();
        calendar.setTime(end);
        calendar.add(Calendar.DAY_OF_MONTH,-3);
        var start = calendar.getTime();
        var events = eventRepository.findByCreatedDateBetween(start,end);
        var result = new ArrayList<Event>();
        for(var e : events){
            if(!ids.contains(e.getId()) && e.getEventType().equals(Event.EventType.EVENT)){
                result.add(e);
                ids.add(e.getId());
                System.err.println("------------------------------->"+ids);
            }
            if(result.size()==3){
                break;
            }
        }
        return result;
    }

    @Override
    public void refresh() {
        ids.clear();
    }

    @Override
    public List<Object> getAllPolls(String page) {
        var pageable = PageRequest.of(Integer.parseInt(page),3, Sort.by("createdDate"));
        var polls = eventRepository.findAll(pageable);
        var objects = new ArrayList<Object>();
        objects.add(page);
        objects.add(polls.getContent());
        return objects;
    }

    @Override
    @Transactional
    public String deleteVoteOfAPerson(Long id) {
        pollRepository.deleteByEventIdAndUserId(id,getLoginUser().getId());
        return id+"";
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(()->new CommunityHubException("event not found"));
    }

    @Override
    public EventDTO makeEventDto(String eventId, String title, String description, String startDate, String endDate, String location, String photo, MultipartFile newPhoto) {
        var eventDto = new EventDTO();
        eventDto.setEventId(eventId);
        eventDto.setTitle(title);
        eventDto.setDescription(description);
        eventDto.setLocation(location);
        eventDto.setStart_date(startDate);
        eventDto.setEnd_date(endDate);
        eventDto.setPhoto(photo);
        eventDto.setNewPhoto(newPhoto);
        return eventDto;
    }

    @Override
    public Event updateEventPost(EventDTO eventDto) throws IOException {
        var event = eventRepository.findById(Long.valueOf(eventDto.getEventId())).orElseThrow(()->new CommunityHubException("event not found"));
        if(eventDto.getNewPhoto()!=null){
            event.setPhoto(uploadPhoto(eventDto.getNewPhoto()));
        }else{
            if(eventDto.getPhoto().endsWith("deleted")){
                event.setPhoto("/static/assets/img/eventImg.jpg");
            }
            event.setPhoto(event.getPhoto());
        }
        event.setTitle(eventDto.getTitle());
        event.setDescription(eventDto.getDescription());
        if(eventDto.getStart_date().equals("old")){
            event.setStart_date(event.getStart_date());
        }else {
            event.setStart_date(java.sql.Date.valueOf(LocalDate.parse(eventDto.getStart_date())));
        }
        if(eventDto.getEnd_date().equals("old")){
            event.setEnd_date(event.getEnd_date());
        }else {
            event.setEnd_date(java.sql.Date.valueOf(LocalDate.parse(eventDto.getEnd_date())));
        }
        event.setLocation(eventDto.getLocation());
        return eventRepository.save(event);
    }

    @Override
    public List<Event> filterDeletedEvents(List<Event> events) {
        var returnList = new ArrayList<Event>();
        for(var e : events){
            if(!e.isDeleted()){
                returnList.add(e);
            }
        }
        return returnList;
    }

    @Override
    @Transactional
    public Event deleteAEvent(Long id) {
        var event = eventRepository.findById(id).orElseThrow(()->new CommunityHubException("event not found"));
        event.setDeleted(true);
        return eventRepository.save(event);
    }

    @Override
    public Event createAPoll(EventDTO eventDTO,List<String> opts,MultipartFile photo) throws IOException, ParseException {
        System.err.println(photo.getOriginalFilename()+"--------------------------------------------------");
        if(StringUtils.hasLength(photo.getOriginalFilename())){
            eventDTO.setMultipartFile(photo);
        }else{
            eventDTO.setMultipartFile(null);
        }
        var event = createEventPost(eventDTO);
        event.setEventType(Event.EventType.VOTE);
        var saved = eventRepository.save(event);
        for(int i =0; i<opts.size(); i++){
            var voteOpt = new VoteOption();
            voteOpt.setVoteEvent(saved);
            voteOpt.setType(opts.get(i));
            voteOptionRepository.save(voteOpt);
        }
        return saved;
    }

    @Override
    public List<Event> getAllPollPost() {
        var list = eventRepository.findAll();
        var result = new ArrayList<Event>();
        for(var l : list){
            if (!l.isDeleted()
                    && l.getAccess().equals(Access.PUBLIC)
                    && l.getEventType().equals(Event.EventType.VOTE)){
                result.add(l);
            }
        }
        return result;
    }

    @Override
    public Event giveVote(Long voteOptionId, Long eventId) {
        var event = eventRepository.findById(eventId).orElseThrow(()->new CommunityHubException("event not found"));
        var voteOption = voteOptionRepository.findById(voteOptionId).orElseThrow(()->new CommunityHubException("vote option not found"));
        var poll = new Poll();
        poll.setEvent(event);
        poll.setVoteOptionId(voteOption);
        poll.setDate(new Date());
        poll.setUser(getLoginUser());
        pollRepository.save(poll);
        return event;
    }

    @Override
    public String getCountOfTheVoteOption(Long voteOptionId) {
        var list = pollRepository.searchByVoteOptionId(voteOptionId);
        System.err.println(list.size());
        return list.size()+"";
    }

    @Override
    public Boolean checkIfUserIsAlreadyVoted(Long eventId) {
        var list = pollRepository.searchByEventIdAndUserId(eventId,getLoginUser().getId());
        System.err.println(list);
        return !list.isEmpty();
    }

    @Override
    public Poll deleteAPoll(Long eventId) {
        pollRepository.deleteAPoll(eventId,getLoginUser().getId());
        return new Poll();
    }

    @Override
    @Transactional
    @Modifying
    public void updatePollOptions(List<String> oldOpts, List<String> newOpts, Long eventId) {
        var list = new ArrayList<Long>();
        var event = eventRepository.findById(eventId).orElseThrow(()->new CommunityHubException("event not found"));
        for(var o : oldOpts){
            list.add(Long.valueOf(o));
        }
        for(var l :list){
            System.err.println("here deleted--------------------------------------------------->");
            System.err.println(l);
            pollRepository.deleteByIdWithQuery(l);
            voteOptionRepository.deleteByIdWithQuery(l);
        }
        for(var n : newOpts){
            System.err.println("here created -------------------------------------------------->");
            var vo = new VoteOption();
            vo.setVoteEvent(event);
            vo.setType(n);
            voteOptionRepository.save(vo);
        }
    }

    @Override
    public boolean checkVotedMark(Long voteOptionId ) {
        var userId = getLoginUser().getId();
        var voteOption = voteOptionRepository.findById(voteOptionId).orElseThrow(()->new CommunityHubException("vote option not found"));
        var poll = pollRepository.searchByUserIdAndEventId(userId,voteOption.getVoteEvent().getId());
        if(poll == null || poll.getVoteOptionId() == null){
            return false;
        }else if(poll.getVoteOptionId().getId().equals(voteOptionId)){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public Page<Event> getEventsForNewsfeed(String page) {
        var all = eventRepository.findAll();
        var filteredEvents = new ArrayList<>(all.stream().filter(e -> e.getEventType().equals(Event.EventType.EVENT) && !e
                .isDeleted() && e
                .getAccess().equals(Access.PUBLIC)).toList());
        filteredEvents.addAll(getEventsOfGroupForLoginUser(Event.EventType.EVENT));
        filteredEvents.sort(Comparator.comparing(Event::getCreated_date).reversed());
        return getPaginationOfEvents(filteredEvents,page);
    }

    @Override
    public Event findById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new CommunityHubException("Event Not Found Exception!"));
    }

    private Page<Event> getPaginationOfEvents(List<Event> filteredEvents,String page){
        var pageable = PageRequest.of(Integer.parseInt(page),5);
        var start = Math.toIntExact(pageable.getOffset());
        if (start >= filteredEvents.size()) {
            return Page.empty(pageable);
        }
        var end = Math.min(start + pageable.getPageSize(), filteredEvents.size());
        var paginatedPosts = filteredEvents.subList(start, end);
        var postPage = new PageImpl<>(paginatedPosts, pageable, filteredEvents.size());
        return postPage;
    }

    private List<Event> getEventsOfGroupForLoginUser(Event.EventType eventType){
        var userId = getLoginUser().getId();
        var list = new ArrayList<Event>();
        var communityIds = user_groupRepository.getCommunityIdFromUserId(getLoginUser().getId());
        var userGroupIds = new ArrayList<Long>();
        for(var c : communityIds){
            userGroupIds.addAll(user_groupRepository.getIdFromCommunityId(c));
        }
        for(var g : userGroupIds){
            list.addAll(eventRepository.findByUserGroupId(g));
            System.err.println(g);
        }
        return list.stream().filter(l->l.getEventType().equals(eventType) && !l.isDeleted()).toList();
    }


    private static Specification<Event> eventUserJoin(String name){
        return (root, query, criteriaBuilder) -> {
            if(name!=null && !name.isEmpty()){
                Join<Event,User> eventUserJoin = root.join("user");
                return criteriaBuilder.like(criteriaBuilder.lower(eventUserJoin.get("name")),"%".concat(name.toLowerCase()).concat("%"));
            }
            return criteriaBuilder.disjunction();
        };
    }


    private Event findByEventId(PollDto pollDto){
        return eventRepository.findById(Long.valueOf(pollDto.getEventId())).orElseThrow(()->new CommunityHubException("event not found"));
    }

    private Event createEventPost(EventDTO eventDTO) throws ParseException, IOException {
        var event = setEventUp(eventDTO);
        return eventRepository.save(event);
    }

    private Event createPollPost(EventDTO eventDTO) throws ParseException, IOException {
        var event = setEventUp(eventDTO);
        event.setEventType(Event.EventType.VOTE);
        return eventRepository.save(event);
    }


    private Event setEventUp(EventDTO eventDTO) throws ParseException, IOException {
        var event = new Event();
        event.setDeleted(false);
        event.setAccess(checkAccess(eventDTO));
        event.setCreated_date(new Date());
        event.setDescription(eventDTO.getDescription());
        event.setStart_date(formatter.parse(eventDTO.getStart_date()));
        event.setEnd_date(formatter.parse(eventDTO.getEnd_date()));
        event.setUser(getLoginUser());
        event.setUser_group(setGroup(eventDTO));
        event.setLocation(eventDTO.getLocation());
        event.setEventType(Event.EventType.EVENT);
        event.setTitle(eventDTO.getTitle());
        if(eventDTO.getMultipartFile()!=null && StringUtils.hasLength(eventDTO.getMultipartFile().getOriginalFilename())){
            System.err.println(eventDTO.getMultipartFile());
            event.setPhoto(uploadPhoto(eventDTO.getMultipartFile()));
            System.err.println(uploadPhoto(eventDTO.getMultipartFile()));
        }else {
            event.setPhoto("/static/assets/img/eventImg.jpg");
        }
        return eventRepository.save(event);
    }

    private Access checkAccess(EventDTO eventDTO){
        if(eventDTO == null || eventDTO.getGroupId().equals("0")){
            return  Access.PUBLIC;
        }else {
            return Access.PRIVATE;
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

    private Date stringToDateChanger(String string){
        var offSetDate = OffsetDateTime.parse(string, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return Date.from(offSetDate.toInstant());
    }

    private List<Event> searchMethod(String in){
        var input = URLDecoder.decode(in, StandardCharsets.UTF_8);
        var specifications = new ArrayList<Specification<Event>>();
        if(StringUtils.hasLength(input)){
            specifications.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("location")),"%".concat(input.toLowerCase()).concat("%")));
            specifications.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),"%".concat(input.toLowerCase()).concat("%")));
            specifications.add((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),"%".concat(input.toLowerCase()).concat("%")));
            specifications.add(eventUserJoin(input));
        }


        Specification<Event> eventSpec = Specification.where(null);
        for(var s : specifications){
            eventSpec = eventSpec.or(s);
        }
        return eventRepository.findAll( eventSpec);
    }
    public User getCurrentLoginUser() {
        return userRepository.findByStaffId(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new CommunityHubException("user not found"));
    }

    public String getFileExtension(MultipartFile file) {
        return file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')).toLowerCase();
    }



    public String uploadPhoto(MultipartFile file) throws IOException {
        return cloudinary.uploader()
                .upload(file.getBytes(), Map.of("public_id", UUID.randomUUID().toString()))
                .get("url").toString();
    }

    public boolean isValidPhotoExtension(String extension) {
        return photoExtensions.contains(extension);
    }
}
