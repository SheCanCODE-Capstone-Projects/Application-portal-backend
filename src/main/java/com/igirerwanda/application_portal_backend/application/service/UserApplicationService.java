package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.*;

public interface UserApplicationService {
    ApplicationDto startApplicationForUser(Long userId);
    ApplicationDto submitApplication(Long applicationId, Long userId);

    // Progressive Steps
    ApplicationDto savePersonalInfo(Long applicationId, Long userId, PersonalInfoDto dto);
    ApplicationDto saveEducation(Long applicationId, Long userId, EducationDto dto);
    ApplicationDto saveMotivation(Long applicationId, Long userId, MotivationDto dto);
    ApplicationDto saveDisability(Long applicationId, Long userId, DisabilityDto dto);
    ApplicationDto saveVulnerability(Long applicationId, Long userId, VulnerabilityDto dto);

    double calculateCompletionPercentage(Long applicationId);
}