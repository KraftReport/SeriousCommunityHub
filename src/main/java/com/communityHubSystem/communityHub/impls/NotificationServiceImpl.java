package com.communityHubSystem.communityHub.impls;

import com.communityHubSystem.communityHub.dto.NotificationDto;
import com.communityHubSystem.communityHub.models.Notification;
import com.communityHubSystem.communityHub.repositories.NotificationRepository;
import com.communityHubSystem.communityHub.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
   private final ModelMapper modelMapper;


    @Override
    public void save(Notification notification) {
        notificationRepository.save(notification);
    }

    @Override
    public Page<NotificationDto> findAllByUserId(Long id, String page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "date");
        Pageable pageable = PageRequest.of(Math.toIntExact(Long.parseLong(page)), 7, sort);
        Page<Notification> notificationList = notificationRepository.findAllByUserId(id, pageable);
        System.out.println("About Total Page"+notificationList.getTotalPages());
        System.out.println("Pagination"+notificationList.getSize());
        return notificationList.map(notification -> modelMapper.map(notification,NotificationDto.class));
    }

    @Override
    public List<Notification> findByReplyIdAndUserId(Long id, Long id1) {
        return notificationRepository.findByReplyIdAndUserId(id,id1);
    }

    @Transactional
    @Override
    public void deleteAll(Long id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public List<Notification> findByCommentIdAndUserId(Long id, Long id1) {
        return notificationRepository.findByCommentIdAndUserId(id,id1);
    }

    @Override
    public List<Notification> findByUserId(Long id) {
        return notificationRepository.findByUserId(id);
    }

    @Transactional
    @Override
    public void deleteAllForLoginUser(List<Notification> notificationList) {
        notificationRepository.deleteAll(notificationList);
    }

    @Override
    public List<Notification> findAllByCommentId(Long id) {
        return notificationRepository.findAllByCommentId(id);
    }

    @Override
    public Notification findByCommentIdAndReplyId(Long id, Long id1) {
        return notificationRepository.findByCommentIdAndReplyId(id,id1);
    }

}
