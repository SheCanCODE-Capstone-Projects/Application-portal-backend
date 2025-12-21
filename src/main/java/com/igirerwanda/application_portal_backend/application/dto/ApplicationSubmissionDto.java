package com.igirerwanda.application_portal_backend.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApplicationSubmissionDto {
    @NotNull(message = "Cohort ID is required")
    private Long cohortId;
    
    @NotNull(message = "Personal information is required")
    @Valid
    private PersonalInfoDto personalInfo;
    
    @NotNull(message = "Education information is required")
    @Valid
    private EducationDto education;
    
    @NotNull(message = "Motivation information is required")
    @Valid
    private MotivationDto motivation;
    
    @Valid
    private List<DocumentDto> documents;
    
    @Valid
    private List<EmergencyContactDto> emergencyContacts;
    
    @Valid
    private DisabilityDto disability;
    
    @Valid
    private VulnerabilityDto vulnerability;
}