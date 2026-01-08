package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.ApplicationDto;
import com.igirerwanda.application_portal_backend.application.dto.InterviewScheduleRequest;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import java.util.List;

public interface AdminApplicationService {
    // Retrieval
    List<ApplicationDto> getAllActiveApplications();
    List<ApplicationDto> getApplicationsByStatus(ApplicationStatus status);
    List<ApplicationDto> getSystemRejectedApplications();
    List<ApplicationDto> getArchivedApplications();
    List<ApplicationDto> getDeletedApplications();
    ApplicationDto getApplicationDetails(Long applicationId);

    // Actions
    ApplicationDto acceptApplication(Long applicationId);
    ApplicationDto rejectApplication(Long applicationId);
    ApplicationDto scheduleInterview(Long applicationId, InterviewScheduleRequest request);

    // Management
    void softDeleteApplication(Long applicationId);
    void archiveApplication(Long applicationId);
    void restoreApplication(Long applicationId); // Restore from deleted/archived
}