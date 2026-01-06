package com.igirerwanda.application_portal_backend.notification.dto;

import com.igirerwanda.application_portal_backend.notification.entity.Notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class NotificationDto {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private Long applicationId;
    private String applicationStatus;
}
