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
    public void sendApplicationAcceptedNotification(Application app) {
        String applicantName = getApplicantName(app);
        String cohortName = app.getCohort().getName();

        String webTitle = "🎉 Application Accepted!";
        String webMessage = "Congratulations! You have been accepted into the " + cohortName + " program.";

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

        notifyUserMultiChannel(app.getUser(), webTitle, webMessage, emailSubject, emailBody, NotificationType.APPLICATION_ACCEPTED, app.getId(), "ACCEPTED");
    }

    @Override
    public void sendApplicationRejectedNotification(Application app) {
        String applicantName = getApplicantName(app);
        String cohortName = app.getCohort().getName();

        String webTitle = "Application Status Update";
        String webMessage = "We have an update regarding your application for " + cohortName + ". Please check your email.";

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

    @Override
    public void sendInterviewScheduledNotification(Application app, String details) {
        String applicantName = getApplicantName(app);
        String cohortName = app.getCohort().getName();

        String webTitle = "Interview Invitation 📅";
        String webMessage = "You have been invited to an interview for " + cohortName + ". Check your email for details.";

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

    private String getApplicantName(Application app) {
        if (app.getPersonalInformation() != null &&
                app.getPersonalInformation().getFullName() != null &&
                !app.getPersonalInformation().getFullName().isEmpty()) {
            return app.getPersonalInformation().getFullName();
        }
        return app.getUser().getRegister().getUsername();
    }

    private void notifyUserMultiChannel(User user,
                                        String webTitle, String webMessage,
                                        String emailSubject, String emailBody,
                                        NotificationType type, UUID appId, String status) {

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
                    user.getId().toString(),
                    "/queue/notifications",
                    mapToDto(saved)
            );
        } catch (Exception e) {
            log.error("Failed to send WebSocket notification", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(UUID userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    public void markAsRead(UUID notificationId, UUID userId) {
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
    public void markAllAsRead(UUID userId) {
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