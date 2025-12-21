package com.igirerwanda.application_portal_backend.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationCreateDto {
    @NotNull(message = "Cohort ID is required")
    private Long cohortId;
}
