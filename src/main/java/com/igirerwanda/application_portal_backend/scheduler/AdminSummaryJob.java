package com.igirerwanda.application_portal_backend.scheduler;

import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.notification.service.WebSocketEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSummaryJob {

    private final ApplicationRepository applicationRepository;
    private final WebSocketEventService webSocketEventService;

    // Run every night at 11:55 PM
    @Scheduled(cron = "0 55 23 * * *")
    public void broadcastDailyAdminStats() {
        log.info("Calculating daily admin statistics...");

        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);

        // 1. Applications received today
        long appsToday = applicationRepository.countByCreatedAtBetween(startOfDay, endOfDay);

        // 2. System rejections today
        long rejectsToday = applicationRepository.countSystemRejectionsBetween(startOfDay, endOfDay);

        // 3. Total Sync count (Total accepted/approved users)
        long totalSynced = applicationRepository.countSynchronizedCandidates();

        Map<String, Object> statsPayload = new HashMap<>();
        statsPayload.put("date", LocalDate.now().toString());
        statsPayload.put("newApplications", appsToday);
        statsPayload.put("systemRejections", rejectsToday);
        statsPayload.put("totalSynchronized", totalSynced);
        statsPayload.put("message", "Daily Summary: " + appsToday + " new applications processed today.");

        // Broadcast to /topic/admin
        webSocketEventService.broadcastToAdmins("ADMIN_DAILY_SUMMARY", statsPayload);

        log.info("Daily admin stats broadcasted: {}", statsPayload);
    }
}