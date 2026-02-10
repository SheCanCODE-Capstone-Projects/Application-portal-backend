package com.igirerwanda.application_portal_backend.scheduler;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationReminderJob {

    private final ApplicationRepository applicationRepository;
    private final NotificationService notificationService;

    // Run every day at 9:00 AM
    @Scheduled(cron = "0 0 9 * * *")
    public void processDailyReminders() {
        log.info("Starting daily application reminders...");

        sendIncompleteDraftReminders();
        sendUnderReviewNudges();
        sendProgramStartCountdowns();

        log.info("Daily application reminders completed.");
    }

    private void sendIncompleteDraftReminders() {
        // Find DRAFT applications that haven't been updated in 3 days
        LocalDateTime threshold = LocalDateTime.now().minusDays(3);
        List<Application> staleDrafts = applicationRepository.findByStatusAndUpdatedAtBefore(ApplicationStatus.DRAFT, threshold);

        for (Application app : staleDrafts) {
            try {
                notificationService.sendIncompleteApplicationReminder(app);
            } catch (Exception e) {
                log.error("Failed to send incomplete reminder for app {}", app.getId(), e);
            }
        }
    }

    private void sendUnderReviewNudges() {
        // Remind users if their application has been under review for more than 7 days without update
        LocalDateTime threshold = LocalDateTime.now().minusDays(7);
        List<Application> stuckReviews = applicationRepository.findByStatusAndUpdatedAtBefore(ApplicationStatus.UNDER_REVIEW, threshold);

        for (Application app : stuckReviews) {
            try {
                notificationService.sendUnderReviewReminder(app);
            } catch (Exception e) {
                log.error("Failed to send review reminder for app {}", app.getId(), e);
            }
        }
    }

    private void sendProgramStartCountdowns() {
        // Check accepted users
        List<Application> acceptedApps = applicationRepository.findByStatusAndDeletedFalse(ApplicationStatus.ACCEPTED);
        LocalDate today = LocalDate.now();

        for (Application app : acceptedApps) {
            if (app.getCohort() != null && app.getCohort().getStartDate() != null) {
                LocalDate startDate = app.getCohort().getStartDate();
                long daysUntil = ChronoUnit.DAYS.between(today, startDate);

                // Send notification if 7 days or 1 day remaining
                if (daysUntil == 7 || daysUntil == 1) {
                    try {
                        notificationService.sendProgramStartReminder(app, daysUntil);
                    } catch (Exception e) {
                        log.error("Failed to send start reminder for app {}", app.getId(), e);
                    }
                }
            }
        }
    }
}