package com.igirerwanda.application_portal_backend.cohort.dto;

import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CohortUpdateDto {

    @Size(max = 100, message = "Name max length is 100")
    private String name;

    @Size(max = 500, message = "Description max length is 500")
    private String description;

    private List<String> requirements;

    private List<String> rules;

    private List<UserRole> roles;

    private Boolean isOpen;
    private Integer applicationLimit;
    private Integer year;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;
}