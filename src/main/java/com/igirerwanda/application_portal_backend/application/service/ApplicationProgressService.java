package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.notification.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApplicationProgressService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private WebSocketService webSocketService;

    public Object getApplicationProgress(String userEmail) {
        Application application = applicationRepository.findByUserRegisterEmail(userEmail)
                .orElse(null);

        Map<String, Object> progress = new HashMap<>();
        if (application != null) {
            progress.put("applicationId", application.getId());
            progress.put("status", application.getStatus().toString());
            progress.put("submittedAt", application.getCreatedAt());
            progress.put("lastUpdated", application.getUpdatedAt());
            
            // Calculate progress percentage based on status
            int progressPercentage = calculateProgressPercentage(application.getStatus());
            progress.put("progressPercentage", progressPercentage);
        } else {
            progress.put("status", "NOT_STARTED");
            progress.put("progressPercentage", 0);
        }

        return progress;
    }

    public void updateApplicationProgress(String userEmail, String newStatus) {
        Object progress = getApplicationProgress(userEmail);
        webSocketService.broadcastApplicationProgress(userEmail, progress);
    }

    private int calculateProgressPercentage(ApplicationStatus status) {
        return switch (status) {
            case DRAFT -> 25;
            case SUBMITTED -> 50;
            case UNDER_REVIEW -> 75;
            case APPROVED, REJECTED -> 100;
            default -> 0;
        };
    }
}