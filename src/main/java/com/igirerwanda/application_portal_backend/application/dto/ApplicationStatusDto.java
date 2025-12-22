package com.igirerwanda.application_portal_backend.application.dto;

import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationStatusDto {
    private Long applicationId;
    private ApplicationStatus status;
    private String statusReason;
    private LocalDateTime statusChangedAt;
    private String changedBy;
}
