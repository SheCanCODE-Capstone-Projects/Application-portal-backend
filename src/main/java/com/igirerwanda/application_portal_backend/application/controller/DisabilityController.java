package com.igirerwanda.application_portal_backend.application.controller;

import com.igirerwanda.application_portal_backend.application.dto.ApplicationDto;
import com.igirerwanda.application_portal_backend.application.dto.DisabilityDto;
import com.igirerwanda.application_portal_backend.application.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/disability")
@RequiredArgsConstructor
public class DisabilityController {

    private final ApplicationService applicationService;

    @PutMapping("/application/{applicationId}")
    public ResponseEntity<ApplicationDto> updateDisability(
            @PathVariable Long applicationId,
            @RequestBody DisabilityDto dto) {
        ApplicationDto result = applicationService.updateDisability(applicationId, dto);
        return ResponseEntity.ok(result);
    }
}
