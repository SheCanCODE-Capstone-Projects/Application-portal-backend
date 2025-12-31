package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final SystemRejectionService systemRejectionService;

    public ApplicationService(ApplicationRepository applicationRepository, SystemRejectionService systemRejectionService) {
        this.applicationRepository = applicationRepository;
        this.systemRejectionService = systemRejectionService;
    }

    @Transactional
    public Application submitApplication(Application application) {
        Application savedApplication = applicationRepository.save(application);
        systemRejectionService.evaluateAndRejectIfNeeded(savedApplication);
        return savedApplication;
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public List<Application> getApplicationsByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatus(status);
    }
}
