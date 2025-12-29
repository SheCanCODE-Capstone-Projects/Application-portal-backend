package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.*;

import java.util.UUID;

public interface ApplicationProgressiveService {
    ApplicationDto savePersonalInfoStep(UUID applicationId, PersonalInfoDto dto);
    ApplicationDto saveEducationStep(UUID applicationId, EducationDto dto);
    ApplicationDto saveMotivationStep(UUID applicationId, MotivationDto dto);
    ApplicationDto saveDisabilityStep(UUID applicationId, DisabilityDto dto);
    ApplicationDto saveVulnerabilityStep(UUID applicationId, VulnerabilityDto dto);
    ApplicationDto getApplicationProgress(UUID applicationId);
    double calculateCompletionPercentage(UUID applicationId);
}
