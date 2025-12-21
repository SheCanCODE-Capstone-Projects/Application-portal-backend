package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.*;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;

import java.util.List;

public interface ApplicationService {
    ApplicationDto createApplication(Long userId, ApplicationCreateDto dto);
    ApplicationDto submitCompleteApplication(Long userId, ApplicationSubmissionDto dto);
    ApplicationDto updatePersonalInfo(Long applicationId, PersonalInfoDto dto);
    ApplicationDto updateEducation(Long applicationId, EducationDto dto);
    ApplicationDto updateMotivation(Long applicationId, MotivationDto dto);
    ApplicationDto addDocument(Long applicationId, DocumentDto dto);
    ApplicationDto addEmergencyContact(Long applicationId, EmergencyContactDto dto);
    ApplicationDto updateDisability(Long applicationId, DisabilityDto dto);
    ApplicationDto updateVulnerability(Long applicationId, VulnerabilityDto dto);
    ApplicationDto submitApplication(Long applicationId);
    ApplicationDto getApplication(Long applicationId);
    List<ApplicationDto> getUserApplications(Long userId);
    List<ApplicationDto> getApplicationsByStatus(ApplicationStatus status);
    boolean isApplicationComplete(Long applicationId);
    void deleteDocument(Long documentId);
    void deleteEmergencyContact(Long contactId);
}
