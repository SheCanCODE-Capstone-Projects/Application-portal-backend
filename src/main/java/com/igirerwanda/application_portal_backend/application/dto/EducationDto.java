package com.igirerwanda.application_portal_backend.application.dto;

import com.igirerwanda.application_portal_backend.common.enums.EducationalLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EducationDto {
    @NotNull(message = "Highest education level is required")
    private EducationalLevel highestEducationLevel;
    
    @NotBlank(message = "Highest education is required")
    private String highestEducation;
    
    @NotBlank(message = "Occupation is required")
    private String occupation;
    
    @NotBlank(message = "Employment status is required")
    private String employmentStatus;
    
    @Min(value = 0, message = "Years of experience cannot be negative")
    private Integer yearsExperience;
}