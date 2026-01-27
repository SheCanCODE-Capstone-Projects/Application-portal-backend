package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.ApplicationDto;
import com.igirerwanda.application_portal_backend.application.dto.InterviewScheduleRequest;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import java.util.List;
import java.util.UUID; // Import UUID

public interface AdminApplicationService {
    List<ApplicationDto> getAllActiveApplications();
    List<ApplicationDto> getApplicationsByStatus(ApplicationStatus status);
    List<ApplicationDto> getSystemRejectedApplications();
    List<ApplicationDto> getArchivedApplications();
    List<ApplicationDto> getDeletedApplications();

    // Changed Long to UUID
    ApplicationDto getApplicationDetails(UUID applicationId);
    ApplicationDto acceptApplication(UUID applicationId);
    ApplicationDto rejectApplication(UUID applicationId);
    ApplicationDto scheduleInterview(UUID applicationId, InterviewScheduleRequest request);

    void softDeleteApplication(UUID applicationId);
    void archiveApplication(UUID applicationId);
    void restoreApplication(UUID applicationId);
}