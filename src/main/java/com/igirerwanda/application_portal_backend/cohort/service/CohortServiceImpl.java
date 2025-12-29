package com.igirerwanda.application_portal_backend.cohort.service;

import com.igirerwanda.application_portal_backend.cohort.dto.CohortCreateDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortUpdateDto;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.cohort.mapper.CohortMapper;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import com.igirerwanda.application_portal_backend.cohort.service.CohortService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CohortServiceImpl implements CohortService {


    @Override
    public CohortDto createCohort(CohortCreateDto dto) {
        return null;
    }

    @Override
    public List<CohortDto> getAllCohorts() {
        return List.of();
    }

    @Override
    public List<CohortDto> getCohortsForFrontend() {
        return List.of();
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
            cohort.setName(dto.getName());
        }
        
        if (dto.getDescription() != null) {
            cohort.setDescription(dto.getDescription());
        }
        
        if (dto.getRequirements() != null) {
            cohort.setRequirements(dto.getRequirements());
        }
        
        if (dto.getRules() != null) {
            cohort.setRules(dto.getRules());
        }
        
        if (dto.getRoles() != null) {
            cohort.setRoles(dto.getRoles());
        }
        
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
