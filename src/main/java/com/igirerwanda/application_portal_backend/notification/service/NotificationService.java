package com.igirerwanda.application_portal_backend.notification.service;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.notification.dto.NotificationDto;
import com.igirerwanda.application_portal_backend.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    void sendApplicationStartedNotification(Application app); // NEW
    void sendApplicationSubmittedNotification(Application app);
    void sendApplicationUnderReviewNotification(Application app);
    void sendApplicationAcceptedNotification(Application app);
    void sendApplicationRejectedNotification(Application app);
    void sendInterviewScheduledNotification(Application app, String details);
    void sendAccountActivatedNotification(User user);

    List<NotificationDto> getUserNotifications(UUID userId);
    List<NotificationDto> getUnreadNotifications(UUID userId);
    long getUnreadCount(UUID userId);
    void markAsRead(UUID notificationId, UUID userId);
    void markAllAsRead(UUID userId);
}