package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.ApplicationDto;
import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import com.igirerwanda.application_portal_backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminApplicationServiceImpl implements AdminApplicationService {

    private final ApplicationRepository applicationRepository;
    private final NotificationService notificationService;

    @Override
    public List<ApplicationDto> getApplicationsByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatus(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationDto getApplicationDetails(Long applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found"));
        return mapToDto(app);
    }

    @Override
    public ApplicationDto updateStatus(Long applicationId, ApplicationStatus status) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found"));

        app.setStatus(status);
        Application savedApp = applicationRepository.save(app);

        // Notify user based on new status
        if (status == ApplicationStatus.ACCEPTED) {
            notificationService.sendApplicationAcceptedNotification(savedApp);
        } else if (status == ApplicationStatus.REJECTED) {
            notificationService.sendApplicationRejectedNotification(savedApp);
        } else if (status == ApplicationStatus.UNDER_REVIEW) {
            notificationService.sendApplicationUnderReviewNotification(savedApp);
        }

        return mapToDto(savedApp);
    }

    @Override
    public ApplicationDto scheduleInterview(Long applicationId, String details) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found"));

        app.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
        Application savedApp = applicationRepository.save(app);


        notificationService.sendInterviewScheduledNotification(savedApp, details);

        return mapToDto(savedApp);
    }

    private ApplicationDto mapToDto(Application app) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(app.getId());
        dto.setUserId(app.getUser().getId());
        dto.setStatus(app.getStatus());

        // Null safe cohort mapping
        if (app.getCohort() != null) {
            dto.setCohortId(app.getCohort().getId());
            dto.setCohortName(app.getCohort().getName());
        }

        return dto;
    }
}