package com.igirerwanda.application_portal_backend.cohort.controller;

import com.igirerwanda.application_portal_backend.cohort.dto.CohortCreateDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortUpdateDto;
import com.igirerwanda.application_portal_backend.cohort.service.CohortService;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public CohortDto createCohort(@RequestBody CohortCreateDto dto) {
        return service.createCohort(dto);
    }

    @GetMapping("/admin/cohorts")
    public List<CohortDto> getAllCohorts() {
        return service.getAllCohorts();
    }

    @GetMapping("/admin/cohorts/{id}")
    public CohortDto getCohortById(@PathVariable Long id) {
        return service.getCohortById(id);
    }

    @PutMapping("/admin/cohorts/{id}")
    public CohortDto updateCohort(@PathVariable Long id, @RequestBody CohortUpdateDto dto) {
        return service.updateCohort(id, dto);
    }

    @DeleteMapping("/admin/cohorts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCohort(@PathVariable Long id) {
        service.deleteCohort(id);
    }

    @GetMapping("/cohorts/frontend")
    public List<CohortDto> getCohortsForFrontend() {
        return service.getCohortsForFrontend();
    }
}
