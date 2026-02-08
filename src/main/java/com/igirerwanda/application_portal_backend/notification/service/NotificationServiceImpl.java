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
import java.util.UUID;
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
    public void sendApplicationStartedNotification(Application app) {
        String title = "Application Started";
        String message = "You have successfully started your application for " + app.getCohort().getName();
        String emailBody = String.format("""
            Dear %s,
            You have successfully started your application for the %s cohort.
            Please complete all sections and submit before the deadline.
            """, getApplicantName(app), app.getCohort().getName());
        notifyUserMultiChannel(app.getUser(), title, message, "Application Started", emailBody, NotificationType.APPLICATION_STARTED, app.getId(), "DRAFT");
    }

    @Override
    public void sendApplicationSubmittedNotification(Application app) {
        String title = "Application Submitted";
        String message = "Your application for " + app.getCohort().getName() + " has been received.";
        String emailBody = String.format("Dear %s,\nYour application has been successfully submitted.", getApplicantName(app));
        notifyUserMultiChannel(app.getUser(), title, message, "Application Received", emailBody, NotificationType.APPLICATION_SUBMITTED, app.getId(), "SUBMITTED");
    }

    @Override
    public void sendApplicationUnderReviewNotification(Application app) {
        String title = "Application Under Review";
        String message = "Your application is now being reviewed by our team.";
        String emailBody = String.format("Dear %s,\nYour application is now under review.", getApplicantName(app));
        notifyUserMultiChannel(app.getUser(), title, message, "Under Review", emailBody, NotificationType.APPLICATION_UNDER_REVIEW, app.getId(), "UNDER_REVIEW");
    }

    @Override
    public void sendApplicationAcceptedNotification(Application app) {
        String title = "🎉 Application Accepted!";
        String message = "Congratulations! You have been accepted into " + app.getCohort().getName();
        String emailBody = String.format("Dear %s,\nCongratulations! You have been accepted.", getApplicantName(app));
        notifyUserMultiChannel(app.getUser(), title, message, "Congratulations!", emailBody, NotificationType.APPLICATION_ACCEPTED, app.getId(), "ACCEPTED");
    }

    @Override
    public void sendApplicationRejectedNotification(Application app) {
        String title = "Application Status Update";
        String message = "Update regarding your application for " + app.getCohort().getName();
        String emailBody = String.format("Dear %s,\nWe regret to inform you that we cannot offer you a position.", getApplicantName(app));
        notifyUserMultiChannel(app.getUser(), title, message, "Status Update", emailBody, NotificationType.APPLICATION_REJECTED, app.getId(), "REJECTED");
    }

    @Override
    public void sendInterviewScheduledNotification(Application app, String details) {
        String title = "Interview Scheduled 📅";
        String message = "You have an interview scheduled. Check email for details.";
        String emailBody = String.format("Dear %s,\nInterview Details:\n%s", getApplicantName(app), details);
        notifyUserMultiChannel(app.getUser(), title, message, "Interview Invitation", emailBody, NotificationType.INTERVIEW_SCHEDULED, app.getId(), "INTERVIEW_SCHEDULED");
    }

    @Override
    public void sendAccountActivatedNotification(User user) {
        notifyUserMultiChannel(user, "Account Activated", "Your account is now active.", "Account Activated", "Welcome to the portal!", NotificationType.GENERAL, null, "ACTIVE");
    }


    @Override
    public void sendIncompleteApplicationReminder(Application app) {
        String title = "Complete Your Application 📝";
        String message = "Your application for " + app.getCohort().getName() + " is incomplete. Don't miss the deadline!";

        String emailBody = String.format("""
            Dear %s,
            
            We noticed you haven't completed your application for the %s cohort yet.
            
            Please log in to the portal and submit your application to be considered.
            
            Best regards,
            The Application Portal Team
            """, getApplicantName(app), app.getCohort().getName());

        notifyUserMultiChannel(app.getUser(), title, message, "Reminder: Complete your Application", emailBody, NotificationType.REMINDER_INCOMPLETE, app.getId(), "DRAFT");
    }

    @Override
    public void sendProgramStartReminder(Application app, long daysRemaining) {
        String title = "Program Starting Soon! 🚀";
        String message = String.format("The %s program starts in %d days. Get ready!", app.getCohort().getName(), daysRemaining);

        String emailBody = String.format("""
            Dear %s,
            
            This is a friendly reminder that the %s cohort begins in %d days.
            Please ensure you are ready for the start date.
            
            We are excited to see you!
            """, getApplicantName(app), app.getCohort().getName(), daysRemaining);

        notifyUserMultiChannel(app.getUser(), title, message, "Program Starting Soon", emailBody, NotificationType.REMINDER_PROGRAM_START, app.getId(), "ACCEPTED");
    }

    @Override
    public void sendUnderReviewReminder(Application app) {
        String title = "Application Update";
        String message = "Your application is still under review. Please check your email for any requests.";

        String emailBody = String.format("""
            Dear %s,
            
            Your application for %s is currently being reviewed by our team.
            Please keep an eye on your email inbox for any requests for additional information.
            
            Thank you for your patience.
            """, getApplicantName(app), app.getCohort().getName());

        notifyUserMultiChannel(app.getUser(), title, message, "Application Status: Under Review", emailBody, NotificationType.REMINDER_UNDER_REVIEW, app.getId(), "UNDER_REVIEW");
    }

    // --- Helper Methods ---

    private String getApplicantName(Application app) {
        if (app.getPersonalInformation() != null && app.getPersonalInformation().getFullName() != null) {
            return app.getPersonalInformation().getFullName();
        }
        return app.getUser().getRegister().getUsername();
    }

    private void notifyUserMultiChannel(User user, String webTitle, String webMessage, String emailSubject, String emailBody, NotificationType type, UUID appId, String status) {
        saveInAppNotification(user, webTitle, webMessage, type, appId, status);
        if (user.getRegister().getEmail() != null) {
            emailService.sendEmail(user.getRegister().getEmail(), emailSubject, emailBody);
        }
    }

    private void saveInAppNotification(User user, String title, String message, NotificationType type, UUID appId, String status) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setApplicationId(appId);
        notification.setApplicationStatus(status);
        notification.setRead(false);

        Notification saved = notificationRepository.save(notification);

        try {
            messagingTemplate.convertAndSendToUser(
                    user.getRegister().getEmail(),
                    "/queue/notifications",
                    mapToDto(saved)
            );
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification", e);
        }
    }

    @Override
    public List<NotificationDto> getUserNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<NotificationDto> getUnreadNotifications(UUID userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    public void markAsRead(UUID notificationId, UUID userId) {
        Notification n = notificationRepository.findById(notificationId).orElseThrow(() -> new NotFoundException("Not found"));
        if(n.getUser().getId().equals(userId)) {
            n.setRead(true);
            n.setReadAt(LocalDateTime.now());
            notificationRepository.save(n);
        }
    }

    @Override
    public void markAllAsRead(UUID userId) {
        List<Notification> list = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        list.forEach(n -> { n.setRead(true); n.setReadAt(LocalDateTime.now()); });
        notificationRepository.saveAll(list);
    }

    private NotificationDto mapToDto(Notification n) {
        return new NotificationDto(n.getId(), n.getTitle(), n.getMessage(), n.getType(), n.isRead(), n.getCreatedAt(), n.getReadAt(), n.getApplicationId(), n.getApplicationStatus());
    }
}