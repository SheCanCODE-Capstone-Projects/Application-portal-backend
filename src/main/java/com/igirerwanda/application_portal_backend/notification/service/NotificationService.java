package com.igirerwanda.application_portal_backend.notification.service;

import com.igirerwanda.application_portal_backend.notification.dto.NotificationDto;
import com.igirerwanda.application_portal_backend.notification.entity.Notification;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    
    void sendApplicationSubmittedNotification(UUID userId, UUID applicationId);
    
    void sendApplicationUnderReviewNotification(UUID userId, UUID applicationId);
    
    void sendApplicationAcceptedNotification(UUID userId, UUID applicationId);
    
    void sendApplicationRejectedNotification(UUID userId, UUID applicationId);
    
    List<NotificationDto> getUserNotifications(UUID userId);
    
    List<NotificationDto> getUnreadNotifications(UUID userId);
    
    long getUnreadCount(UUID userId);
    
    void markAsRead(UUID notificationId, UUID userId);
    
    void markAllAsRead(UUID userId);
}
