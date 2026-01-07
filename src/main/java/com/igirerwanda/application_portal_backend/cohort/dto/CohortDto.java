package com.igirerwanda.application_portal_backend.cohort.dto;

import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import lombok.Data;

import java.util.List;

@Data
public class CohortDto {
    private Long id;
    private String name;
    private String description;
    private List<String> requirements;
    private List<String> rules;
    private List<UserRole> roles;
    private Boolean isOpen;
    private Integer applicationLimit;
    private Integer year;
}
