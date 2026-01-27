package com.igirerwanda.application_portal_backend.cohort.service;

import com.igirerwanda.application_portal_backend.cohort.dto.CohortCreateDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortUpdateDto;

import java.util.List;
import java.util.UUID; // Import UUID

public interface CohortService {
    CohortDto createCohort(CohortCreateDto dto);

    List<CohortDto> getAllCohorts();

    List<CohortDto> getCohortsForFrontend();

    CohortDto getCohortById(UUID id);

    CohortDto updateCohort(UUID id, CohortUpdateDto dto);

    void deleteCohort(UUID id);
}