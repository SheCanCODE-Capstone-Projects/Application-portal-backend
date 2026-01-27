package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.*;
import java.util.List;
import java.util.UUID;

public interface UserApplicationService {
    ApplicationDto startApplicationForUser(UUID userId);

    ApplicationSubmissionResponseDto submitApplication(UUID applicationId, UUID userId);

    ApplicationDto getApplicationForUser(UUID userId);

    // Progressive Steps
    ApplicationDto savePersonalInfo(UUID applicationId, UUID userId, PersonalInfoDto dto);
    ApplicationDto saveEducation(UUID applicationId, UUID userId, EducationDto dto);
    ApplicationDto saveMotivation(UUID applicationId, UUID userId, MotivationDto dto);
    ApplicationDto saveDisability(UUID applicationId, UUID userId, DisabilityDto dto);
    ApplicationDto saveVulnerability(UUID applicationId, UUID userId, VulnerabilityDto dto);
    ApplicationDto saveDocuments(UUID applicationId, UUID userId, List<DocumentDto> dtos);
    ApplicationDto saveEmergencyContacts(UUID applicationId, UUID userId, List<EmergencyContactDto> dtos);

    double calculateCompletionPercentage(UUID applicationId, UUID userId);
}