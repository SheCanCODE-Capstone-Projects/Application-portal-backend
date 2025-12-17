package com.igirerwanda.application_portal_backend.application.controller;

import com.igirerwanda.application_portal_backend.application.dto.ApplicationDto;
import com.igirerwanda.application_portal_backend.application.dto.EducationDto;
import com.igirerwanda.application_portal_backend.application.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/education")
@RequiredArgsConstructor
public class EducationalController {

    private final ApplicationService applicationService;

    @PutMapping("/application/{applicationId}")
    public ResponseEntity<ApplicationDto> updateEducation(
            @PathVariable Long applicationId,
            @RequestBody EducationDto dto) {
        ApplicationDto result = applicationService.updateEducation(applicationId, dto);
        return ResponseEntity.ok(result);
    }
}
