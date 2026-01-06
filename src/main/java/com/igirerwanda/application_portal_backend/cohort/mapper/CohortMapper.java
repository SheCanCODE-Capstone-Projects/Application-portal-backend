package com.igirerwanda.application_portal_backend.cohort.mapper;

import com.igirerwanda.application_portal_backend.cohort.dto.CohortCreateDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortDto;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import org.springframework.stereotype.Component;

@Component
public class CohortMapper {

    public static Cohort toEntity(CohortCreateDto dto) {
        return Cohort.builder()
                .name(dto.getName())
                .domain(dto.getDescription())
                .build();
    }

    public static CohortDto toDto(Cohort entity) {
        CohortDto dto = new CohortDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDomain());
        return dto;
    }
}