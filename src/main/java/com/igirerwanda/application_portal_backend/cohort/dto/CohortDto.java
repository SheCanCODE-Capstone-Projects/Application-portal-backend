package com.igirerwanda.application_portal_backend.cohort.dto;

import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class CohortDto {
    private UUID id;
    private String name;
    private String description;
    private List<String> requirements;
    private List<String> rules;
    private List<UserRole> roles;
    private Boolean isOpen;
    private Integer applicationLimit;
    private Integer year;
    private LocalDate startDate;
    private LocalDate endDate;
}