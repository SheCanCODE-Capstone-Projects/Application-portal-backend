package com.igirerwanda.application_portal_backend.application.dto;

import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ApplicationDto {
    private UUID id;
    private UUID userId;
    private UUID cohortId;
    private String cohortName;
    private ApplicationStatus status;
    private boolean isSystemRejected;
    private String systemRejectionReason;
    private LocalDateTime submittedAt;
    private LocalDateTime createdAt;
    private PersonalInfoDto personalInfo;
    private EducationDto education;
    private MotivationDto motivation;
    private List<DocumentDto> documents;
    private List<EmergencyContactDto> emergencyContacts;
    private DisabilityDto disability;
    private VulnerabilityDto vulnerability;
}