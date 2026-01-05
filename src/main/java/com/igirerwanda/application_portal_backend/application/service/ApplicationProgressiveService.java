package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.*;

public interface ApplicationProgressiveService {
    ApplicationDto savePersonalInfoStep(Long applicationId, PersonalInfoDto dto);
    ApplicationDto saveEducationStep(Long applicationId, EducationDto dto);
    ApplicationDto saveMotivationStep(Long applicationId, MotivationDto dto);
    ApplicationDto saveDisabilityStep(Long applicationId, DisabilityDto dto);
    ApplicationDto saveVulnerabilityStep(Long applicationId, VulnerabilityDto dto);
    ApplicationDto getApplicationProgress(Long applicationId);
    double calculateCompletionPercentage(Long applicationId);
}
