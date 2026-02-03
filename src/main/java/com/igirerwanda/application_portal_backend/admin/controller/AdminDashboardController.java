package com.igirerwanda.application_portal_backend.admin.controller;

import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import com.igirerwanda.application_portal_backend.user.repository.UserRepository;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        // 1. Basic Counts
        long totalApplicants = applicationRepo.count();
        long activeCohorts = cohortRepo.count();
        long systemRejects = applicationRepo.countByIsSystemRejectedTrue();
        long successfulRegisters = userRepo.count();
        long duplicateApplications = applicationRepo.countDuplicateRejections(); // Synchronization Check

        // 2. Graph Data: Applications by Day
        List<Object[]> dailyDataRaw = applicationRepo.countApplicationsByDay();
        List<Map<String, Object>> dailyTrend = dailyDataRaw.stream().map(record -> Map.of(
                "date", record[0].toString(),
                "count", record[1]
        )).collect(Collectors.toList());

        // 3. Graph Data: Status by Cohort
        List<Object[]> cohortDataRaw = applicationRepo.countApplicationsByCohortAndStatus();
        Map<String, Map<String, Long>> cohortStatsMap = new HashMap<>();

        for (Object[] row : cohortDataRaw) {
            String cohortName = (String) row[0];
            String status = ((ApplicationStatus) row[1]).name();
            Long count = (Long) row[2];

            cohortStatsMap.putIfAbsent(cohortName, new HashMap<>());
            cohortStatsMap.get(cohortName).put(status, count);
        }

        // Flatten for frontend chart
        List<Map<String, Object>> cohortBreakdown = new ArrayList<>();
        cohortStatsMap.forEach((name, stats) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", name);
            item.put("ACCEPTED", stats.getOrDefault("ACCEPTED", 0L) + stats.getOrDefault("APPROVED", 0L));
            item.put("REJECTED", stats.getOrDefault("REJECTED", 0L));
            item.put("PENDING", stats.getOrDefault("PENDING", 0L) + stats.getOrDefault("PENDING_REVIEW", 0L));
            cohortBreakdown.add(item);
        });

        return ResponseEntity.ok(Map.of("data", Map.of(
                "totalApplicants", totalApplicants,
                "activeCohorts", activeCohorts,
                "systemRejects", systemRejects,
                "successfulRegisters", successfulRegisters,
                "duplicateRejections", duplicateApplications, // "One who applied twice"
                "charts", Map.of(
                        "dailyTrend", dailyTrend,
                        "cohortBreakdown", cohortBreakdown
                ),
                "trends", Map.of(
                        "applicants", "+5%",
                        "cohorts", "Active",
                        "rejects", Math.round((double)systemRejects/totalApplicants * 100) + "% Rate",
                        "registers", "Total Users"
                )
        )));
    }
}