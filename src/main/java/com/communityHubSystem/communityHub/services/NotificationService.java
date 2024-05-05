package com.communityHubSystem.communityHub.services;

import com.communityHubSystem.communityHub.dto.NotificationDto;
import com.communityHubSystem.communityHub.models.Notification;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NotificationService {

    public void save(Notification notification);

   public Page<NotificationDto> findAllByUserId(Long id, String page);

   public List<Notification> findByReplyIdAndUserId(Long id, Long id1);

    public void deleteAll(Long id);

   public List<Notification> findByCommentIdAndUserId(Long id, Long id1);

//   public Page<NotificationDto> findAllNotifications(Long id, int page);

}
