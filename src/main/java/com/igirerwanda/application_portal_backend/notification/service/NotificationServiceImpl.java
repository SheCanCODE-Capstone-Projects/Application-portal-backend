package com.igirerwanda.application_portal_backend.notification.service;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import com.igirerwanda.application_portal_backend.notification.dto.NotificationDto;
import com.igirerwanda.application_portal_backend.notification.entity.Notification;
import com.igirerwanda.application_portal_backend.notification.entity.Notification.NotificationType;
import com.igirerwanda.application_portal_backend.notification.repository.NotificationRepository;
import com.igirerwanda.application_portal_backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendAccountActivatedNotification(User user) {
        String title = "Account Activated";
        String message = "Welcome to the Application Portal! Your account is now active. You can now select a cohort.";

        notifyUserMultiChannel(user, title, message, NotificationType.GENERAL, null, "ACTIVE");
    }

    @Override
    public void sendApplicationSubmittedNotification(Application app) {
        String title = "Application Submitted";
        String message = String.format("Your application for %s has been successfully submitted.", app.getCohort().getName());

        notifyUserMultiChannel(app.getUser(), title, message, NotificationType.APPLICATION_SUBMITTED, app.getId(), "SUBMITTED");
    }

    @Override
    public void sendApplicationUnderReviewNotification(Application app) {
        String title = "Application Under Review";
        String message = String.format("Your application for %s is now being reviewed by our team.", app.getCohort().getName());

        notifyUserMultiChannel(app.getUser(), title, message, NotificationType.APPLICATION_UNDER_REVIEW, app.getId(), "UNDER_REVIEW");
    }

    @Override
    public void sendApplicationAcceptedNotification(Application app) {
        String title = "Congratulations! Application Approved";
        String message = String.format("Your application for %s has been APPROVED! Welcome to the program.", app.getCohort().getName());

        notifyUserMultiChannel(app.getUser(), title, message, NotificationType.APPLICATION_ACCEPTED, app.getId(), "ACCEPTED");
    }

    @Override
    public void sendApplicationRejectedNotification(Application app) {
        String title = "Application Update";
        String message = String.format("Regarding your application for %s: We have updated your status.", app.getCohort().getName());

        notifyUserMultiChannel(app.getUser(), title, message, NotificationType.APPLICATION_REJECTED, app.getId(), "REJECTED");
    }

    @Override
    public void sendInterviewScheduledNotification(Application app, String details) {
        String title = "Interview Scheduled";
        String message = String.format("An interview for %s is scheduled. Details: %s", app.getCohort().getName(), details);

        notifyUserMultiChannel(app.getUser(), title, message, NotificationType.INTERVIEW_SCHEDULED, app.getId(), "INTERVIEW_SCHEDULED");
    }

    // --- Helper for sending to all channels ---
    private void notifyUserMultiChannel(User user, String title, String message, NotificationType type, Long appId, String status) {
        // 1. In-App Notification (Database + WebSocket)
        saveInAppNotification(user, title, message, type, appId, status);

        // 2. Email Notification
        if (user.getRegister().getEmail() != null) {
            emailService.sendEmail(user.getRegister().getEmail(), title, message);
        }

        // 3. SMS Notification
        // Check Register phone first, then PersonalInfo phone if Register is null
//        String phone = user.getRegister().getPhone();

        // Fallback to application phone if available and register phone is null
//        if (phone == null && appId != null) {
//            // Logic to fetch from personal info could go here if needed,
//            // but usually we rely on the main account phone.
//        }
//
//        if (phone != null && !phone.isEmpty()) {
//            smsService.sendSms(phone, message);
//        }
    }

    private void saveInAppNotification(User user, String title, String message, NotificationType type, Long appId, String status) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setApplicationId(appId);
        notification.setApplicationStatus(status);
        notification.setRead(false);

        Notification saved = notificationRepository.save(notification);

        // Real-time broadcast
        try {
            messagingTemplate.convertAndSendToUser(
                    user.getId().toString(),
                    "/queue/notifications",
                    mapToDto(saved)
            );
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification", e);
        }
    }

    // --- Management Methods ---

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(userId)) {
            throw new SecurityException("Access denied");
        }

        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        LocalDateTime now = LocalDateTime.now();
        unread.forEach(n -> {
            n.setRead(true);
            n.setReadAt(now);
        });
        notificationRepository.saveAll(unread);
    }

    private NotificationDto mapToDto(Notification n) {
        return new NotificationDto(
                n.getId(), n.getTitle(), n.getMessage(), n.getType(),
                n.isRead(), n.getCreatedAt(), n.getReadAt(),
                n.getApplicationId(), n.getApplicationStatus()
        );
    }
}