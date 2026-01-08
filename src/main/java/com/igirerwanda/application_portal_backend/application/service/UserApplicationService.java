package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.*;

import java.util.List;

public interface UserApplicationService {
    ApplicationDto startApplicationForUser(Long userId);
    ApplicationDto submitApplication(Long applicationId, Long userId);
    ApplicationDto getApplicationForUser(Long userId);

    // Progressive Steps
    ApplicationDto savePersonalInfo(Long applicationId, Long userId, PersonalInfoDto dto);
    ApplicationDto saveEducation(Long applicationId, Long userId, EducationDto dto);
    ApplicationDto saveMotivation(Long applicationId, Long userId, MotivationDto dto);
    ApplicationDto saveDisability(Long applicationId, Long userId, DisabilityDto dto);
    ApplicationDto saveVulnerability(Long applicationId, Long userId, VulnerabilityDto dto);
    ApplicationDto saveDocuments(Long applicationId, Long userId, List<DocumentDto> dtos);
    ApplicationDto saveEmergencyContacts(Long applicationId, Long userId, List<EmergencyContactDto> dtos);

    // FIX: This method must accept userId to verify ownership
    double calculateCompletionPercentage(Long applicationId, Long userId);
}