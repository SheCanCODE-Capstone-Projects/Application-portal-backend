package com.igirerwanda.application_portal_backend.cohort.controller;

import com.igirerwanda.application_portal_backend.cohort.dto.CohortCreateDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortDto;
import com.igirerwanda.application_portal_backend.cohort.service.CohortService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CohortController {

    private final CohortService service;

    public CohortController(CohortService service) {
        this.service = service;
    }

    @PostMapping("/admin/cohorts")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CohortDto createCohort(@RequestBody CohortCreateDto dto) {
        return service.createCohort(dto);
    }

    @GetMapping("/admin/cohorts")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CohortDto> getAllCohorts() {
        return service.getAllCohorts();
    }

    @GetMapping("/cohorts/frontend")
    public List<CohortDto> getCohortsForFrontend() {
        return service.getCohortsForFrontend();
    }
}
