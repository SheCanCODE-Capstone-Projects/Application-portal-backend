package com.igirerwanda.application_portal_backend.cohort.mapper;

import com.igirerwanda.application_portal_backend.cohort.dto.CohortCreateDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortDto;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;

@Component
public class CohortMapper {

    public static Cohort toEntity(CohortCreateDto dto) {
        return Cohort.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .year(dto.getYear())
                .applicationLimit(dto.getApplicationLimit())
                .isOpen(dto.getIsOpen() != null ? dto.getIsOpen() : true)
                // ✅ Fix: Convert List from JSON to Set for Database
                .rules(dto.getRules() != null ? new HashSet<>(dto.getRules()) : new HashSet<>())
                .roles(dto.getRoles() != null ? new HashSet<>(dto.getRoles()) : new HashSet<>())
                .requirements(dto.getRequirements() != null ? new HashSet<>(dto.getRequirements()) : new HashSet<>())
                .build();
    }

    public static CohortDto toDto(Cohort entity) {
        CohortDto dto = new CohortDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setYear(entity.getYear());

        // ✅ Fix: Convert Set from Database to List for JSON
        dto.setRules(entity.getRules() != null ? new ArrayList<>(entity.getRules()) : new ArrayList<>());
        dto.setRequirements(entity.getRequirements() != null ? new ArrayList<>(entity.getRequirements()) : new ArrayList<>());
        dto.setRoles(entity.getRoles() != null ? new ArrayList<>(entity.getRoles()) : new ArrayList<>());

        dto.setIsOpen(entity.getIsOpen());
        dto.setApplicationLimit(entity.getApplicationLimit());

        return dto;
    }
}