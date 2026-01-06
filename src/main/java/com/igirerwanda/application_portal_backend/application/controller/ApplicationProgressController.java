package com.igirerwanda.application_portal_backend.application.controller;

import com.igirerwanda.application_portal_backend.application.service.ApplicationProgressService;
import com.igirerwanda.application_portal_backend.common.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/application-progress-controller")
@CrossOrigin(origins = "*")
public class ApplicationProgressController {

    @Autowired
    private ApplicationProgressService applicationProgressService;

    @GetMapping("/getApplicationProgress")
    public ResponseEntity<ApiResponse<Object>> getApplicationProgress(Authentication authentication) {
        Object progress = applicationProgressService.getApplicationProgress(authentication.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Application progress retrieved successfully", progress));
    }
}