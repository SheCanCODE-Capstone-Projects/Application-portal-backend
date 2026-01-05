package com.igirerwanda.application_portal_backend.notification.service;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.notification.dto.NotificationDto;
import com.igirerwanda.application_portal_backend.notification.entity.Notification;
import com.igirerwanda.application_portal_backend.notification.entity.Notification.NotificationType;
import com.igirerwanda.application_portal_backend.notification.repository.NotificationRepository;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
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
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendApplicationSubmittedNotification(Application application) {
        String title = "Application Submitted Successfully";
        String message = String.format("Your application for %s has been submitted and is now under review.", 
                application.getCohort().getName());
        
        createAndSendNotification(application, title, message, NotificationType.APPLICATION_SUBMITTED);
        
        // Send email
        String emailSubject = "Application Submitted - " + application.getCohort().getName();
        String emailBody = String.format("""
            Dear %s,
            
            Your application for %s has been successfully submitted.
            
            Application ID: %d
            Submitted on: %s
            
            We will review your application and notify you of the next steps.
            
            Best regards,
            Application Portal Team
            """, 
            application.getUser().getUsername(),
            application.getCohort().getName(),
            application.getId(),
            application.getSubmittedAt());
        
        emailService.sendEmail(application.getUser().getEmail(), emailSubject, emailBody);
    }

    @Override
    public void sendApplicationUnderReviewNotification(Application application) {
        String title = "Application Under Review";
        String message = String.format("Your application for %s is currently being reviewed by our team.", 
                application.getCohort().getName());
        
        createAndSendNotification(application, title, message, NotificationType.APPLICATION_UNDER_REVIEW);
        
        // Send email
        String emailSubject = "Application Under Review - " + application.getCohort().getName();
        String emailBody = String.format("""
            Dear %s,
            
            Your application for %s is now under review.
            
            Application ID: %d
            Status: Under Review
            
            We will notify you once the review is complete.
            
            Best regards,
            Application Portal Team
            """, 
            application.getUser().getUsername(),
            application.getCohort().getName(),
            application.getId());
        
        emailService.sendEmail(application.getUser().getEmail(), emailSubject, emailBody);
    }

    @Override
    public void sendApplicationAcceptedNotification(Application application) {
        String title = "Congratulations! Application Accepted";
        String message = String.format("Your application for %s has been accepted! Welcome to the program.", 
                application.getCohort().getName());
        
        createAndSendNotification(application, title, message, NotificationType.APPLICATION_ACCEPTED);
        
        // Send email
        String emailSubject = "Application Accepted - Welcome to " + application.getCohort().getName();
        String emailBody = String.format("""
            Dear %s,
            
            Congratulations! Your application for %s has been ACCEPTED.
            
            Application ID: %d
            Status: Accepted
            
            Welcome to the program! You will receive further instructions soon.
            
            Best regards,
            Application Portal Team
            """, 
            application.getUser().getUsername(),
            application.getCohort().getName(),
            application.getId());
        
        emailService.sendEmail(application.getUser().getEmail(), emailSubject, emailBody);
    }

    @Override
    public void sendApplicationRejectedNotification(Application application) {
        String title = "Application Update";
        String message = String.format("Thank you for your interest in %s. Unfortunately, your application was not selected this time.", 
                application.getCohort().getName());
        
        createAndSendNotification(application, title, message, NotificationType.APPLICATION_REJECTED);
        
        // Send email
        String emailSubject = "Application Update - " + application.getCohort().getName();
        String emailBody = String.format("""
            Dear %s,
            
            Thank you for your interest in %s.
            
            Application ID: %d
            Status: Not Selected
            
            While your application was not selected this time, we encourage you to apply for future opportunities.
            
            Best regards,
            Application Portal Team
            """, 
            application.getUser().getUsername(),
            application.getCohort().getName(),
            application.getId());
        
        emailService.sendEmail(application.getUser().getEmail(), emailSubject, emailBody);
    }

    @Override
    public void sendInterviewScheduledNotification(Application application, String interviewDetails) {
        String title = "Interview Scheduled";
        String message = String.format("An interview has been scheduled for your %s application. %s", 
                application.getCohort().getName(), interviewDetails);
        
        createAndSendNotification(application, title, message, NotificationType.INTERVIEW_SCHEDULED);
        
        // Send email
        String emailSubject = "Interview Scheduled - " + application.getCohort().getName();
        String emailBody = String.format("""
            Dear %s,
            
            An interview has been scheduled for your %s application.
            
            Application ID: %d
            Interview Details: %s
            
            Please prepare accordingly and contact us if you have any questions.
            
            Best regards,
            Application Portal Team
            """, 
            application.getUser().getUsername(),
            application.getCohort().getName(),
            application.getId(),
            interviewDetails);
        
        emailService.sendEmail(application.getUser().getEmail(), emailSubject, emailBody);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        return notifications.stream().map(this::mapToDto).collect(Collectors.toList());
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
            throw new SecurityException("Access denied: You can only access your own notifications");
        }
        
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        LocalDateTime now = LocalDateTime.now();
        
        unreadNotifications.forEach(notification -> {
            notification.setRead(true);
            notification.setReadAt(now);
        });
        
        notificationRepository.saveAll(unreadNotifications);
    }

    private void createAndSendNotification(Application application, String title, String message, NotificationType type) {
        // Create in-app notification
        Notification notification = new Notification();
        notification.setUser(application.getUser());
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setApplicationId(application.getId());
        notification.setApplicationStatus(application.getStatus().toString());
        
        notification = notificationRepository.save(notification);
        
        // Send real-time notification via WebSocket
        try {
            NotificationDto dto = mapToDto(notification);
            messagingTemplate.convertAndSendToUser(
                    application.getUser().getId().toString(),
                    "/queue/notifications",
                    dto
            );
            log.info("Real-time notification sent to user {}", application.getUser().getId());
        } catch (Exception e) {
            log.error("Failed to send real-time notification to user {}: {}", 
                    application.getUser().getId(), e.getMessage());
        }
    }

    private NotificationDto mapToDto(Notification notification) {
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