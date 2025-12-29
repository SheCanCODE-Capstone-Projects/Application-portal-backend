package com.igirerwanda.application_portal_backend.notification.service;

import com.igirerwanda.application_portal_backend.notification.dto.NotificationDto;
import com.igirerwanda.application_portal_backend.notification.entity.Notification;
import com.igirerwanda.application_portal_backend.notification.repository.NotificationRepository;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void sendApplicationSubmittedNotification(UUID userId, UUID applicationId) {
        User user = userService.findById(userId);
        
        // Create web notification
        Notification notification = createNotification(
            user, 
            "Application Submitted Successfully",
            "Your application has been submitted and is now being processed. You will be notified of any updates.",
            Notification.NotificationType.APPLICATION_SUBMITTED,
            applicationId,
            "SUBMITTED"
        );
        
        // Send email
        emailService.sendEmail(
            user.getEmail(),
            "Application Submitted - Confirmation",
            String.format(
                "Dear %s,\n\nYour application (ID: %s) has been successfully submitted. " +
                "We will review it and notify you of any updates.\n\nThank you for your application.",
                user.getUsername(), applicationId
            )
        );
        
        // Send real-time notification
        sendRealTimeNotification(userId, convertToDto(notification));
    }

    @Override
    @Transactional
    public void sendApplicationUnderReviewNotification(UUID userId, UUID applicationId) {
        User user = userService.findById(userId);
        
        Notification notification = createNotification(
            user,
            "Application Under Review",
            "Your application is now under review by our team. We will notify you once the review is complete.",
            Notification.NotificationType.APPLICATION_UNDER_REVIEW,
            applicationId,
            "UNDER_REVIEW"
        );
        
        emailService.sendEmail(
            user.getEmail(),
            "Application Under Review",
            String.format(
                "Dear %s,\n\nYour application (ID: %s) is now under review. " +
                "Our team will carefully evaluate your submission and notify you of the outcome.\n\nThank you for your patience.",
                user.getUsername(), applicationId
            )
        );
        
        sendRealTimeNotification(userId, convertToDto(notification));
    }

    @Override
    @Transactional
    public void sendApplicationAcceptedNotification(UUID userId, UUID applicationId) {
        User user = userService.findById(userId);
        
        Notification notification = createNotification(
            user,
            "Application Accepted - Congratulations!",
            "Congratulations! Your application has been accepted. Welcome to our program!",
            Notification.NotificationType.APPLICATION_ACCEPTED,
            applicationId,
            "APPROVED"
        );
        
        emailService.sendEmail(
            user.getEmail(),
            "Application Accepted - Welcome!",
            String.format(
                "Dear %s,\n\nCongratulations! Your application (ID: %s) has been accepted. " +
                "Welcome to our program! You will receive further instructions shortly.\n\nWe look forward to working with you.",
                user.getUsername(), applicationId
            )
        );
        
        sendRealTimeNotification(userId, convertToDto(notification));
    }

    @Override
    @Transactional
    public void sendApplicationRejectedNotification(UUID userId, UUID applicationId) {
        User user = userService.findById(userId);
        
        Notification notification = createNotification(
            user,
            "Application Status Update",
            "Thank you for your application. Unfortunately, we are unable to proceed with your application at this time.",
            Notification.NotificationType.APPLICATION_REJECTED,
            applicationId,
            "REJECTED"
        );
        
        emailService.sendEmail(
            user.getEmail(),
            "Application Status Update",
            String.format(
                "Dear %s,\n\nThank you for your interest in our program. " +
                "After careful consideration, we are unable to proceed with your application (ID: %s) at this time.\n\n" +
                "We encourage you to apply again in the future.",
                user.getUsername(), applicationId
            )
        );
        
        sendRealTimeNotification(userId, convertToDto(notification));
    }

    @Override
    public List<NotificationDto> getUserNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationDto> getUnreadNotifications(UUID userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        notificationRepository.findById(notificationId)
                .filter(notification -> notification.getUser().getId().equals(userId))
                .ifPresent(notification -> {
                    notification.setRead(true);
                    notification.setReadAt(LocalDateTime.now());
                    notificationRepository.save(notification);
                });
    }

    @Override
    @Transactional
    public void markAllAsRead(UUID userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        
        unreadNotifications.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        });
        
        notificationRepository.saveAll(unreadNotifications);
    }

    private Notification createNotification(User user, String title, String message, 
                                         Notification.NotificationType type, UUID applicationId, String status) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setApplicationId(applicationId);
        notification.setApplicationStatus(status);
        
        return notificationRepository.save(notification);
    }

    private void sendRealTimeNotification(UUID userId, NotificationDto notificationDto) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notifications",
            notificationDto
        );
    }

    private NotificationDto convertToDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setReadAt(notification.getReadAt());
        dto.setApplicationId(notification.getApplicationId());
        dto.setApplicationStatus(notification.getApplicationStatus());
        return dto;
    }
}
