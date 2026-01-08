package com.igirerwanda.application_portal_backend.scheduler;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.application.service.SystemRejectionService; // <--- MUST BE PRESENT
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SystemRejectionJob {

    private final ApplicationRepository applicationRepository;
    private final SystemRejectionService systemRejectionService;

    public SystemRejectionJob(ApplicationRepository applicationRepository, SystemRejectionService systemRejectionService) {
        this.applicationRepository = applicationRepository;
        this.systemRejectionService = systemRejectionService;
    }

    @Scheduled(fixedRate = 300000)
    public void evaluatePendingApplications() {
        List<Application> pendingApplications = applicationRepository.findByStatus(ApplicationStatus.PENDING_REVIEW);

        for (Application application : pendingApplications) {
            systemRejectionService.evaluateAndRejectIfNeeded(application);
        }
    }
}