package com.igirerwanda.application_portal_backend.application.controller;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.service.ApplicationService;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/admin/applications")
    public List<Application> getAllApplications() {
        return applicationService.getAllApplications();
    }

    @GetMapping("/admin/applications/status/{status}")
    public List<Application> getApplicationsByStatus(@PathVariable ApplicationStatus status) {
        return applicationService.getApplicationsByStatus(status);
    }

    @GetMapping("/admin/applications/system-rejected")
    public List<Application> getSystemRejectedApplications() {
        return applicationService.getApplicationsByStatus(ApplicationStatus.SYSTEM_REJECTED);
    }
}
