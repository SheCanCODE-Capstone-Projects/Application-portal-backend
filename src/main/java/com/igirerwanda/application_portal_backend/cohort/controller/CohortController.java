package com.igirerwanda.application_portal_backend.cohort.controller;

import com.igirerwanda.application_portal_backend.cohort.dto.*;
import com.igirerwanda.application_portal_backend.cohort.service.CohortService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class CohortController {

    private final CohortService service;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/admin/cohorts")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public CohortDto createCohort(@RequestBody CohortCreateDto dto) {
        CohortDto created = service.createCohort(dto);
        messagingTemplate.convertAndSend("/topic/cohorts", created);
        return created;
    }

    @PutMapping("/admin/cohorts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CohortDto updateCohort(@PathVariable Long id, @RequestBody CohortUpdateDto dto) {
        CohortDto updated = service.updateCohort(id, dto);
        messagingTemplate.convertAndSend("/topic/cohorts/update", updated);
        return updated;
    }

    @DeleteMapping("/admin/cohorts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCohort(@PathVariable Long id) {
        service.deleteCohort(id);
        messagingTemplate.convertAndSend("/topic/cohorts/delete", id);
    }

    @GetMapping("/admin/cohorts")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CohortDto> getAllCohorts() {
        return service.getAllCohorts();
    }

    //  Public Endpoints (No Admin Role Required)

    @GetMapping("/cohorts/frontend")
    public List<CohortDto> getCohortsForFrontend() {
        return service.getCohortsForFrontend();
    }

    @GetMapping("/cohorts/{id}")
    public CohortDto getCohortById(@PathVariable Long id) {
        return service.getCohortById(id);
    }
}