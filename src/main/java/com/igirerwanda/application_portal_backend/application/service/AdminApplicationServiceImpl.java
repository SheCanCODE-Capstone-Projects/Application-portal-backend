package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.*;
import com.igirerwanda.application_portal_backend.application.entity.*;
import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import com.igirerwanda.application_portal_backend.me.service.MasterDataService;
import com.igirerwanda.application_portal_backend.notification.service.NotificationService;
import io.micrometer.common.util.internal.logging.InternalLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID; // Import UUID
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminApplicationServiceImpl implements AdminApplicationService {

    private final ApplicationRepository applicationRepository;
    private final NotificationService notificationService;
    private final MasterDataService masterDataService;

    @Override
    public List<ApplicationDto> getAllActiveApplications() {
        return applicationRepository.findByDeletedFalseAndArchivedFalse().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ... (Keep getApplicationsByStatus, getSystemRejectedApplications, getArchivedApplications, getDeletedApplications)

    @Override
    public ApplicationDto getApplicationDetails(UUID applicationId) { // Fixed: UUID
        return mapToDto(findById(applicationId));
    }

    @Override
    public ApplicationDto acceptApplication(UUID applicationId) { // Fixed: UUID
        Application app = findById(applicationId);
        app.setStatus(ApplicationStatus.ACCEPTED);
        Application saved = applicationRepository.save(app);

        try {
            log.info("Attempting to sync accepted user {} to Master Data...", app.getId());
            masterDataService.syncUserToMaster(saved);
        } catch (Exception e) {
            log.error("Failed to sync user to Master DB: {}", e.getMessage());
            // We do NOT roll back transaction here; acceptance is valid even if sync fails temporarily
        }

        notificationService.sendApplicationAcceptedNotification(saved);
        return mapToDto(saved);
    }

    @Override
    public ApplicationDto rejectApplication(UUID applicationId) { // Fixed: UUID
        Application app = findById(applicationId);
        app.setStatus(ApplicationStatus.REJECTED);
        Application saved = applicationRepository.save(app);
        notificationService.sendApplicationRejectedNotification(saved);
        return mapToDto(saved);
    }

    @Override
    public ApplicationDto scheduleInterview(UUID applicationId, InterviewScheduleRequest request) { // Fixed: UUID
        Application app = findById(applicationId);
        app.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
        app.setInterviewDate(request.getInterviewDate());
        Application saved = applicationRepository.save(app);

        String instructions = request.getInstructions() != null ? request.getInstructions() : "No instructions provided";
        String details = String.format("Date: %s. Instructions: %s", request.getInterviewDate().toString(), instructions);

        notificationService.sendInterviewScheduledNotification(saved, details);
        return mapToDto(saved);
    }

    @Override
    public void softDeleteApplication(UUID applicationId) { // Fixed: UUID
        Application app = findById(applicationId);
        app.setDeleted(true);
        applicationRepository.save(app);
    }

    @Override
    public void archiveApplication(UUID applicationId) { // Fixed: UUID
        Application app = findById(applicationId);
        app.setArchived(true);
        applicationRepository.save(app);
    }

    @Override
    public void restoreApplication(UUID applicationId) { // Fixed: UUID
        Application app = findById(applicationId);
        app.setDeleted(false);
        app.setArchived(false);
        applicationRepository.save(app);
    }

    // Helper using UUID
    private Application findById(UUID id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Application not found with id: " + id));
    }

    // Reuse your existing mapToDto method
    private ApplicationDto mapToDto(Application app) {
        // ... copy existing implementation ...
        // Ensure you use UUIDs in DTOs
        ApplicationDto dto = new ApplicationDto();
        dto.setId(app.getId()); // UUID
        dto.setUserId(app.getUser().getId()); // UUID
        // ... rest of mapping
        return dto;
    }

    // ... Implement missing get* methods by copy-pasting from your previous file but keeping structure
    @Override
    public List<ApplicationDto> getApplicationsByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatusAndDeletedFalse(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDto> getSystemRejectedApplications() {
        return applicationRepository.findByIsSystemRejectedTrueAndDeletedFalse().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDto> getArchivedApplications() {
        return applicationRepository.findByArchivedTrueAndDeletedFalse().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDto> getDeletedApplications() {
        return applicationRepository.findByDeletedTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}