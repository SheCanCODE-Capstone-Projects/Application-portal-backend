package com.igirerwanda.application_portal_backend.cohort.service;

import com.igirerwanda.application_portal_backend.cohort.dto.CohortCreateDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortDto;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.cohort.mapper.CohortMapper;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import com.igirerwanda.application_portal_backend.cohort.service.CohortService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CohortServiceImpl implements CohortService {

    private final CohortRepository repository;

    public CohortServiceImpl(CohortRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public CohortDto createCohort(CohortCreateDto dto) {
        repository.findByName(dto.getName()).ifPresent(c -> {
            throw new RuntimeException("DuplicateResourceException: Cohort already exists");
        });
        Cohort cohort = CohortMapper.toEntity(dto);
        return CohortMapper.toDto(repository.save(cohort));
    }

    @Override
    public List<CohortDto> getAllCohorts() {
        return repository.findAll().stream()
                .map(CohortMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CohortDto> getCohortsForFrontend() {
        return repository.findAll().stream()
                .map(cohort -> {
                    CohortDto dto = new CohortDto();
                    dto.setId(cohort.getId());
                    dto.setName(cohort.getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
