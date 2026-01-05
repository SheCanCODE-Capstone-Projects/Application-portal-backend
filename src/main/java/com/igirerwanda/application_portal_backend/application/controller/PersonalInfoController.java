package com.igirerwanda.application_portal_backend.application.controller;

import com.igirerwanda.application_portal_backend.application.dto.ApplicationDto;
import com.igirerwanda.application_portal_backend.application.dto.PersonalInfoDto;
import com.igirerwanda.application_portal_backend.application.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/personal-info")
@RequiredArgsConstructor
public class PersonalInfoController {

    private final ApplicationService applicationService;

    @PutMapping("/application/{applicationId}")
    public ResponseEntity<ApplicationDto> updatePersonalInfo(
            @PathVariable Long applicationId,
            @RequestBody PersonalInfoDto dto) {
        ApplicationDto result = applicationService.updatePersonalInfo(applicationId, dto);
        return ResponseEntity.ok(result);
    }
}
