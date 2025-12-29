package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.*;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {
    ApplicationDto createApplication(UUID userId, ApplicationCreateDto dto);
    ApplicationDto submitCompleteApplication(UUID userId, ApplicationSubmissionDto dto);
    ApplicationDto updatePersonalInfo(UUID applicationId, PersonalInfoDto dto);
    ApplicationDto updateEducation(UUID applicationId, EducationDto dto);
    ApplicationDto updateMotivation(UUID applicationId, MotivationDto dto);
    ApplicationDto addDocument(UUID applicationId, DocumentDto dto);
    ApplicationDto addEmergencyContact(UUID applicationId, EmergencyContactDto dto);
    ApplicationDto updateDisability(UUID applicationId, DisabilityDto dto);
    ApplicationDto updateVulnerability(UUID applicationId, VulnerabilityDto dto);
    ApplicationDto submitApplication(UUID applicationId);
    ApplicationDto getApplication(UUID applicationId);
    List<ApplicationDto> getUserApplications(UUID userId);
    List<ApplicationDto> getApplicationsByStatus(ApplicationStatus status);
    boolean isApplicationComplete(UUID applicationId);
    void deleteDocument(UUID documentId);
    void deleteEmergencyContact(UUID contactId);
    
    // Status update methods for admin actions
    ApplicationDto updateApplicationStatus(UUID applicationId, ApplicationStatus status);
    ApplicationDto approveApplication(UUID applicationId);
    ApplicationDto rejectApplication(UUID applicationId);
    ApplicationDto moveToReview(UUID applicationId);
}
