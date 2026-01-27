package com.igirerwanda.application_portal_backend.notification.service;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.notification.dto.NotificationDto;
import com.igirerwanda.application_portal_backend.user.entity.User;
import java.util.List;
import java.util.UUID;

public interface NotificationService {
    // Triggers
    void sendAccountActivatedNotification(User user);
    void sendApplicationSubmittedNotification(Application application);
    void sendApplicationUnderReviewNotification(Application application);
    void sendApplicationAcceptedNotification(Application application);
    void sendApplicationRejectedNotification(Application application);
    void sendInterviewScheduledNotification(Application application, String interviewDetails);

    // Management
    List<NotificationDto> getUserNotifications(UUID userId);
    List<NotificationDto> getUnreadNotifications(UUID userId);
    long getUnreadCount(UUID userId);
    void markAsRead(UUID notificationId, UUID userId);
    void markAllAsRead(UUID userId);
}