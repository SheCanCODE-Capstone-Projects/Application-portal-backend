package com.igirerwanda.application_portal_backend.admin.controller;

import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import com.igirerwanda.application_portal_backend.user.repository.UserRepository;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final ApplicationRepository applicationRepo;
    private final CohortRepository cohortRepo;
    private final UserRepository userRepo;

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(Map.of("data", Map.of(
                "totalApplicants", applicationRepo.count(),
                "activeCohorts", cohortRepo.count(),
                "systemRejects", applicationRepo.countByStatus(ApplicationStatus.SYSTEM_REJECTED),
                "successfulRegisters", userRepo.count(),
                "trends", Map.of(
                        "applicants", "+5%",
                        "cohorts", "+1",
                        "rejects", "-2%",
                        "registers", "+10%"
                )
        )));
    }
}