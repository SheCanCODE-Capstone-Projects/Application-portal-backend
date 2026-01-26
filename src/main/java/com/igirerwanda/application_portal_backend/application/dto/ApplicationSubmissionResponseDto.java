package com.igirerwanda.application_portal_backend.application.dto;

import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationSubmissionResponseDto {
    private ApplicationStatus status;
    private String rejectionReason;
    private ApplicationDto application;
    
    public ApplicationSubmissionResponseDto(ApplicationStatus status, String rejectionReason, ApplicationDto application) {
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.application = application;
    }
    
    public static ApplicationSubmissionResponseDto systemRejected(String rejectionReason, ApplicationDto application) {
        return new ApplicationSubmissionResponseDto(ApplicationStatus.SYSTEM_REJECTED, rejectionReason, application);
    }
    
    public static ApplicationSubmissionResponseDto submitted(ApplicationDto application) {
        return new ApplicationSubmissionResponseDto(ApplicationStatus.SUBMITTED, null, application);
    }
}