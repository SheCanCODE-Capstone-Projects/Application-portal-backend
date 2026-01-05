package com.igirerwanda.application_portal_backend.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EducationDto {
    @NotBlank(message = "Highest education is required")
    private String highestEducation;
    
    @NotBlank(message = "Occupation is required")
    private String occupation;
    
    @NotBlank(message = "Employment status is required")
    private String employmentStatus;
    
    @Min(value = 0, message = "Years of experience cannot be negative")
    private Integer yearsExperience;
}
