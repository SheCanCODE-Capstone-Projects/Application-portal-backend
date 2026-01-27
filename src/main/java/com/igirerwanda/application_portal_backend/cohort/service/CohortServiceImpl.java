package com.igirerwanda.application_portal_backend.cohort.service;

import com.igirerwanda.application_portal_backend.common.exception.DuplicateResourceException;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import com.igirerwanda.application_portal_backend.cohort.dto.*;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.cohort.mapper.CohortMapper;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CohortServiceImpl implements CohortService {

    private final CohortRepository repository;

    @Override
    @Transactional
    public CohortDto createCohort(CohortCreateDto dto) {
        repository.findByName(dto.getName()).ifPresent(c -> {
            throw new DuplicateResourceException("Cohort already exists with name: " + dto.getName());
        });

        Cohort cohort = CohortMapper.toEntity(dto);
        return CohortMapper.toDto(repository.save(cohort));
    }

    @Override
    @Transactional
    public CohortDto updateCohort(UUID id, CohortUpdateDto dto) {
        Cohort cohort = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cohort not found with id: " + id));

        if (dto.getName() != null) cohort.setName(dto.getName());
        if (dto.getDescription() != null) cohort.setDescription(dto.getDescription());
        if (dto.getRequirements() != null) cohort.setRequirements(new HashSet<>(dto.getRequirements()));
        if (dto.getRules() != null) cohort.setRules(new HashSet<>(dto.getRules()));
        if (dto.getIsOpen() != null) cohort.setIsOpen(dto.getIsOpen());
        if (dto.getApplicationLimit() != null) cohort.setApplicationLimit(dto.getApplicationLimit());

        return CohortMapper.toDto(repository.save(cohort));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CohortDto> getAllCohorts() {
        return repository.findAll().stream()
                .map(CohortMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CohortDto> getCohortsForFrontend() {
        return repository.findAll().stream()
                .filter(Cohort::getIsOpen)
                .map(CohortMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CohortDto getCohortById(UUID id) {
        return repository.findById(id)
                .map(CohortMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Cohort not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteCohort(UUID id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Cohort not found with id: " + id);
        }
        repository.deleteById(id);
    }
}