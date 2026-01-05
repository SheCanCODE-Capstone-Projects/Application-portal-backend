package com.igirerwanda.application_portal_backend.cohort.service;

import com.igirerwanda.application_portal_backend.cohort.dto.CohortCreateDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortUpdateDto;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.cohort.mapper.CohortMapper;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import com.igirerwanda.application_portal_backend.common.exception.DuplicateResourceException;
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
            throw new DuplicateResourceException("Cohort already exists");
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

    @Override
    public CohortDto getCohortById(Long id) {
        Cohort cohort = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cohort not found with id: " + id));
        return CohortMapper.toDto(cohort);
    }

    @Override
    @Transactional
    public CohortDto updateCohort(Long id, CohortUpdateDto dto) {
        Cohort cohort = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cohort not found with id: " + id));
        
        if (dto.getName() != null && !dto.getName().equals(cohort.getName())) {
            repository.findByName(dto.getName()).ifPresent(c -> {
                throw new DuplicateResourceException("Cohort name already exists");
            });
        }
        
        CohortMapper.updateEntity(cohort, dto);
        
        return CohortMapper.toDto(repository.save(cohort));
    }

    @Override
    @Transactional
    public void deleteCohort(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Cohort not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
