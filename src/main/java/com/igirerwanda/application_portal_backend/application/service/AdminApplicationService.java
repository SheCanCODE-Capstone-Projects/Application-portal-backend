package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.ApplicationDto;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import java.util.List;

public interface AdminApplicationService {
    List<ApplicationDto> getApplicationsByStatus(ApplicationStatus status);
    ApplicationDto getApplicationDetails(Long applicationId);
    ApplicationDto updateStatus(Long applicationId, ApplicationStatus status);
    ApplicationDto scheduleInterview(Long applicationId, String details);
}