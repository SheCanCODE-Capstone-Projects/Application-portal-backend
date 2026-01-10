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
    private final SimpMessagingTemplate messagingTemplate;

    // --- ACCEPT APPLICATION NOTIFICATION ---
    @Override
    public void sendApplicationAcceptedNotification(Application app) {
        String applicantName = getApplicantName(app);
        String cohortName = app.getCohort().getName();

        //  Web Notification (Short & Sweet)
        String webTitle = "🎉 Application Accepted!";
        String webMessage = "Congratulations! You have been accepted into the " + cohortName + " program.";

        //  Email Notification (Detailed & Professional)
        String emailSubject = "Congratulations! Acceptance to " + cohortName;
        String emailBody = String.format("""
            Dear %s,
            
            We are thrilled to inform you that your application for the %s cohort has been ACCEPTED!
            
            The selection process was highly competitive, and your application truly stood out to our review committee. We are confident that you will be a valuable addition to our community.
            
            Next Steps:
            1. Log in to your portal to accept the offer.
            2. Review the onboarding schedule.
            3. Prepare your workstation for the start date.
            
            We look forward to seeing you succeed!
            
            Best regards,
            The SheCanCODE Team
            """, applicantName, cohortName);

        // Send it to both channels
        notifyUserMultiChannel(app.getUser(), webTitle, webMessage, emailSubject, emailBody, NotificationType.APPLICATION_ACCEPTED, app.getId(), "ACCEPTED");
    }

    // --- REJECT APPLICATION NOTIFICATION ---
    @Override
    public void sendApplicationRejectedNotification(Application app) {
        String applicantName = getApplicantName(app);
        String cohortName = app.getCohort().getName();

        // Web Notification
        String webTitle = "Application Status Update";
        String webMessage = "We have an update regarding your application for " + cohortName + ". Please check your email.";

        // Email Notification (Empathetic & Encouraging)
        String emailSubject = "Update on your application for " + cohortName;
        String emailBody = String.format("""
            Dear %s,
            
            Thank you for giving us the opportunity to review your application for the %s cohort.
            
            We received a large number of impressive applications this year, making our final selection very difficult. After careful consideration, we regret to inform you that we are unable to offer you a position in this specific cohort.
            
            Please know that this decision is a reflection of the specific composition of this cohort and not a judgment on your potential. We encourage you to continue honing your skills and to apply again for our future programs.
            
            We wish you the very best in your future endeavors.
            
            Sincerely,
            The SheCanCODE Admissions Team
            """, applicantName, cohortName);

        notifyUserMultiChannel(app.getUser(), webTitle, webMessage, emailSubject, emailBody, NotificationType.APPLICATION_REJECTED, app.getId(), "REJECTED");
    }

    // --- SCHEDULE INTERVIEW NOTIFICATION ---
    @Override
    public void sendInterviewScheduledNotification(Application app, String details) {
        String applicantName = getApplicantName(app);
        String cohortName = app.getCohort().getName();

        // Web Notification
        String webTitle = "Interview Invitation 📅";
        String webMessage = "You have been invited to an interview for " + cohortName + ". Check your email for details.";

        //  Email Notification (Clear & Actionable)
        String emailSubject = "Interview Invitation: " + cohortName;
        String emailBody = String.format("""
            Dear %s,
            
            We have reviewed your initial application and would like to invite you to the next stage of our process: The Interview!
            
            We are excited to learn more about you. Here are the details for your session:
            
            %s
            
            Please ensure you are in a quiet environment with a stable internet connection 10 minutes prior to the start time.
            
            Good luck!
            
            Best regards,
            The SheCanCODE Team
            """, applicantName, details);

        notifyUserMultiChannel(app.getUser(), webTitle, webMessage, emailSubject, emailBody, NotificationType.INTERVIEW_SCHEDULED, app.getId(), "INTERVIEW_SCHEDULED");
    }

    // --- Other Notifications (Standard) ---

    @Override
    public void sendAccountActivatedNotification(User user) {
        String title = "Account Activated";
        String message = "Welcome to the Application Portal! Your account is now active. You can now select a cohort.";
        notifyUserMultiChannel(user, title, message, title, message, NotificationType.GENERAL, null, "ACTIVE");
    }

    @Override
    public void sendApplicationSubmittedNotification(Application app) {
        String title = "Application Submitted";
        String message = "Your application for " + app.getCohort().getName() + " has been successfully submitted.";
        notifyUserMultiChannel(app.getUser(), title, message, title, message, NotificationType.APPLICATION_SUBMITTED, app.getId(), "SUBMITTED");
    }

    @Override
    public void sendApplicationUnderReviewNotification(Application app) {
        String title = "Application Under Review";
        String message = "Your application is now being reviewed by our team.";
        notifyUserMultiChannel(app.getUser(), title, message, title, message, NotificationType.APPLICATION_UNDER_REVIEW, app.getId(), "UNDER_REVIEW");
    }

    //  Helper Methods

    /**
     * Tries to get the user's real full name from PersonalInfo.
     * Fallbacks to their username if PersonalInfo is not yet filled.
     */
    private String getApplicantName(Application app) {
        if (app.getPersonalInformation() != null &&
                app.getPersonalInformation().getFullName() != null &&
                !app.getPersonalInformation().getFullName().isEmpty()) {
            return app.getPersonalInformation().getFullName();
        }
        // Fallback to username from the account
        return app.getUser().getRegister().getUsername();
    }

    /**
     * Handles sending to BOTH Web (WebSocket/DB) and Email channels.
     * Allows distinct content for Web (short) vs Email (long/detailed).
     */
    private void notifyUserMultiChannel(User user,
                                        String webTitle, String webMessage,
                                        String emailSubject, String emailBody,
                                        NotificationType type, Long appId, String status) {

        // 1. Save In-App Notification (Short message)
        saveInAppNotification(user, webTitle, webMessage, type, appId, status);

        // 2. Send Email (Detailed message)
        if (user.getRegister().getEmail() != null) {
            emailService.sendEmail(user.getRegister().getEmail(), emailSubject, emailBody);
        }
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

        // Real-time WebSocket broadcast
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

    // --- Standard Getter/Setter logic ---

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