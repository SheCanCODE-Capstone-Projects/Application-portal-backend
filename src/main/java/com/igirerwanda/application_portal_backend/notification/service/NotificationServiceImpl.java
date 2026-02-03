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
        String message = "You have successfully started your application for " + app.getCohort().getName() + ". Please complete all sections and submit before the deadline.";

        String emailSubject = "Application Started - " + app.getCohort().getName();
        String emailBody = String.format("""
            Dear %s,
            
            You have successfully started your application for the %s cohort.
            
            Please note that your application is currently in DRAFT status. You must complete all required sections (Personal Info, Education, Motivation, Documents) and click SUBMIT for us to review it.
            
            We look forward to receiving your full application!
            
            Best regards,
            The Application Portal Team
            """, app.getUser().getRegister().getUsername(), app.getCohort().getName());

        notifyUserMultiChannel(app.getUser(), title, message, emailSubject, emailBody, NotificationType.APPLICATION_STARTED, app.getId(), "DRAFT");
    }

    // ... (Keep existing methods: Accepted, Rejected, Interview, AccountActivated, Submitted) ...

    @Override
    public void sendApplicationAcceptedNotification(Application app) {
        String applicantName = getApplicantName(app);
        String cohortName = app.getCohort().getName();
        String title = "🎉 Application Accepted!";
        String message = "Congratulations! You have been accepted into " + cohortName + ". Check your email for next steps.";

        String emailBody = String.format("""
            Dear %s,
            
            Congratulations! We are thrilled to inform you that you have been ACCEPTED into the %s cohort.
            
            Please log in to your portal to accept the offer and view the onboarding schedule.
            
            Welcome to the community!
            """, applicantName, cohortName);

        notifyUserMultiChannel(app.getUser(), title, message, "Congratulations! Application Accepted", emailBody, NotificationType.APPLICATION_ACCEPTED, app.getId(), "ACCEPTED");
    }

    @Override
    public void sendApplicationRejectedNotification(Application app) {
        String applicantName = getApplicantName(app);
        String cohortName = app.getCohort().getName();
        String title = "Application Status Update";
        String message = "Update regarding your application for " + cohortName + ".";

        String emailBody = String.format("""
            Dear %s,
            
            Thank you for your interest in the %s cohort.
            
            After careful review, we regret to inform you that we are unable to offer you a position at this time.
            
            We wish you the best in your future endeavors.
            """, applicantName, cohortName);

        notifyUserMultiChannel(app.getUser(), title, message, "Application Status Update", emailBody, NotificationType.APPLICATION_REJECTED, app.getId(), "REJECTED");
    }

    @Override
    public void sendInterviewScheduledNotification(Application app, String details) {
        String applicantName = getApplicantName(app);
        String title = "Interview Scheduled 📅";
        String message = "You have an interview scheduled. Check email for details.";

        String emailBody = String.format("""
            Dear %s,
            
            We would like to invite you for an interview.
            
            Details:
            %s
            
            Please be prepared 10 minutes early.
            """, applicantName, details);

        notifyUserMultiChannel(app.getUser(), title, message, "Interview Invitation", emailBody, NotificationType.INTERVIEW_SCHEDULED, app.getId(), "INTERVIEW_SCHEDULED");
    }

    @Override
    public void sendApplicationSubmittedNotification(Application app) {
        String title = "Application Submitted";
        String message = "Your application for " + app.getCohort().getName() + " has been received.";

        String emailBody = String.format("""
            Dear %s,
            
            Your application for %s has been successfully submitted.
            
            Our team will review it shortly. You will be notified of any status changes.
            """, getApplicantName(app), app.getCohort().getName());

        notifyUserMultiChannel(app.getUser(), title, message, "Application Received", emailBody, NotificationType.APPLICATION_SUBMITTED, app.getId(), "SUBMITTED");
    }

    @Override
    public void sendApplicationUnderReviewNotification(Application app) {
        String title = "Application Under Review";
        String message = "Your application is now being reviewed by our admissions team.";

        String emailBody = String.format("""
            Dear %s,
            
            Your application for %s has moved to the review stage. We are currently assessing your profile and documents.
            
            You will hear from us soon regarding the next steps.
            """, getApplicantName(app), app.getCohort().getName());

        notifyUserMultiChannel(app.getUser(), title, message, "Application Under Review", emailBody, NotificationType.APPLICATION_UNDER_REVIEW, app.getId(), "UNDER_REVIEW");
    }

    @Override
    public void sendAccountActivatedNotification(User user) {
        notifyUserMultiChannel(user, "Account Activated", "Your account is active.", "Account Activated", "Welcome! Your account is active.", NotificationType.GENERAL, null, "ACTIVE");
    }

    // --- Helper Methods ---

    private String getApplicantName(Application app) {
        if (app.getPersonalInformation() != null && app.getPersonalInformation().getFullName() != null) {
            return app.getPersonalInformation().getFullName();
        }
        return app.getUser().getRegister().getUsername();
    }

    private void notifyUserMultiChannel(User user, String webTitle, String webMessage, String emailSubject, String emailBody, NotificationType type, UUID appId, String status) {
        // 1. Save to DB & Send via WebSocket
        saveInAppNotification(user, webTitle, webMessage, type, appId, status);

        // 2. Send Email
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
            // Send to user's EMAIL address as the principal
            messagingTemplate.convertAndSendToUser(
                    user.getRegister().getEmail(),
                    "/queue/notifications",
                    mapToDto(saved)
            );
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification", e);
        }
    }

    // ... (Getters and MarkAsRead methods remain unchanged) ...
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
        if(n.getUser().getId().equals(userId)) { n.setRead(true); n.setReadAt(LocalDateTime.now()); notificationRepository.save(n); }
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