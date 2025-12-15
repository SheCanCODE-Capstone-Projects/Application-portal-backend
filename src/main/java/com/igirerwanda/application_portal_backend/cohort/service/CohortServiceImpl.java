package com.igirerwanda.application_portal_backend.cohort.service;

import com.igirerwanda.application_portal_backend.cohort.dto.CohortCreateDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortDto;
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
}
