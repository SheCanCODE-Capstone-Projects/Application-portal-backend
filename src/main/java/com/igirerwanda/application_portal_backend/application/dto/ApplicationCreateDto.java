package com.igirerwanda.application_portal_backend.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ApplicationCreateDto {
    @NotNull(message = "Cohort ID is required")
    private UUID cohortId;
}
