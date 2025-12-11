package com.igirerwanda.application_portal_backend.cohort.mapper;

import com.igirerwanda.application_portal_backend.cohort.dto.CohortCreateDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortRuleDto;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CohortMapper {

    public static Cohort toEntity(CohortCreateDto dto) {
        return Cohort.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .requirements(dto.getRequirements())
                .rules(dto.getRules())
                .roles(dto.getRoles())
                .build();
    }

    public static CohortDto toDto(Cohort entity) {
        CohortDto dto = new CohortDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setRequirements(entity.getRequirements());

        dto.setRules(entity.getRules().stream().map(rule -> {
            CohortRuleDto ruleDto = new CohortRuleDto();
            ruleDto.setRule(rule);
            ruleDto.setDescription(null);
            return ruleDto;
        }).collect(Collectors.toList()));

        dto.setRoles(entity.getRoles());
        return dto;
    }
}
