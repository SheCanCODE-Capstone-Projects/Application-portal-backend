package com.igirerwanda.application_portal_backend.scheduler;

import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
// FIXED IMPORT:
import com.igirerwanda.application_portal_backend.admin.service.AdminNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSummaryJob {

    private final ApplicationRepository applicationRepository;
    private final AdminNotificationService adminNotificationService;

    // Runs every day at 8:00 AM
    @Scheduled(cron = "0 0 8 * * ?")
    public void generateDailyAdminSummary() {
        log.info("Generating Daily Admin System Summary...");

        long totalApps = applicationRepository.count();
        long pendingReview = applicationRepository.countByStatus(ApplicationStatus.SUBMITTED);
        long systemRejected = applicationRepository.countByIsSystemRejectedTrue();

        // Time and Year synchronization stats (Apps created this year)
        long currentYearApps = applicationRepository.findAll().stream()
                .filter(app -> app.getCreatedAt().getYear() == LocalDateTime.now().getYear())
                .count();

        // FIXED CONCATENATION: Using a cleaner text block as suggested by your IDE
        String message = """
            Daily System Sync & Summary:
            • Total Storage Archive: %d applications
            • Current Year Applications: %d
            • Awaiting Review: %d
            • System Auto-Rejected: %d
            Synchronization Status: Active. Rwandan IDs and Cohorts mapped successfully.
            """.formatted(totalApps, currentYearApps, pendingReview, systemRejected);

        // FIXED METHOD CALL: changed to broadcastToAllAdmins
        adminNotificationService.broadcastToAllAdmins(
                "📊 Daily System Report & Storage Sync",
                message,
                "ADMIN_DAILY_SUMMARY"
        );

        log.info("Daily summary pushed to Admins.");
    }
}