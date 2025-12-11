package com.igirerwanda.application_portal_backend.cohort.service;

import com.igirerwanda.application_portal_backend.cohort.dto.CohortCreateDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortDto;

import java.util.List;

public interface CohortService {
    CohortDto createCohort(CohortCreateDto dto);
    List<CohortDto> getAllCohorts();
    List<CohortDto> getCohortsForFrontend();
}
