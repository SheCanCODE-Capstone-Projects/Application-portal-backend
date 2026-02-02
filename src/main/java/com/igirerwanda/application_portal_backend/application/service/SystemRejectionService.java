package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.cohort.service.CohortRuleService;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemRejectionService {

    private final ApplicationRepository applicationRepository;
    private final CohortRuleService cohortRuleService;

    public SystemRejectionService(ApplicationRepository applicationRepository, CohortRuleService cohortRuleService) {
        this.applicationRepository = applicationRepository;
        this.cohortRuleService = cohortRuleService;
    }

    @Transactional
    public void evaluateAndRejectIfNeeded(Application application) {
        if (application.getCohort() == null) return;

        // FIX: Capture the String result directly
        String rejectionReason = cohortRuleService.evaluateApplication(application, application.getCohort());

        // If rejectionReason is NOT null, it means the application failed the rules
        if (rejectionReason != null) {
            application.setStatus(ApplicationStatus.SYSTEM_REJECTED);
            application.setSystemRejectionReason(rejectionReason); // Use the returned reason
            application.setSystemRejected(true);
            applicationRepository.save(application);
        }
    }
}