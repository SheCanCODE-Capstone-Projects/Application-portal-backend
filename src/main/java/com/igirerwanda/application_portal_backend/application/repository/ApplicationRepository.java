package com.igirerwanda.application_portal_backend.application.repository;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    Optional<Application> findByUserId(UUID userId);
    Optional<Application> findByUserIdAndCohortId(UUID userId, UUID cohortId);

    // Existing methods...
    List<Application> findByStatusAndDeletedFalse(ApplicationStatus status);
    List<Application> findByIsSystemRejectedTrueAndDeletedFalse();
    List<Application> findByArchivedTrueAndDeletedFalse();
    List<Application> findByDeletedTrue();
    List<Application> findByDeletedFalseAndArchivedFalse();

    @Query("SELECT a FROM Application a WHERE a.id = :id AND a.deleted = false")
    Optional<Application> findByIdActive(UUID id);

    long countByIsSystemRejectedTrue();

    // --- QUERIES FOR NOTIFICATIONS & ANALYTICS ---

    // 1. Find applications stuck in a specific status (e.g., DRAFT or UNDER_REVIEW) updated before a certain date
    @Query("SELECT a FROM Application a WHERE a.status = :status AND a.updatedAt < :threshold AND a.deleted = false AND a.archived = false")
    List<Application> findByStatusAndUpdatedAtBefore(@Param("status") ApplicationStatus status, @Param("threshold") LocalDateTime threshold);

    // 2. Count applications created within a date range (for Admin Daily Summary)
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 3. Count system rejections within a date range
    @Query("SELECT COUNT(a) FROM Application a WHERE a.isSystemRejected = true AND a.updatedAt BETWEEN :start AND :end")
    long countSystemRejectionsBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 4. Count synchronized users (Users who exist in Master Data - approximation via status)
    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = 'ACCEPTED' OR a.status = 'APPROVED'")
    long countSynchronizedCandidates();

    // 5. Native queries for graphs (Already existed, keeping them)
    @Query(value = "SELECT DATE(created_at) as date, COUNT(*) as count FROM applications GROUP BY DATE(created_at) ORDER BY date ASC", nativeQuery = true)
    List<Object[]> countApplicationsByDay();

    @Query("SELECT c.name, a.status, COUNT(a) FROM Application a JOIN a.cohort c GROUP BY c.name, a.status")
    List<Object[]> countApplicationsByCohortAndStatus();

    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = 'SYSTEM_REJECTED' AND a.systemRejectionReason LIKE '%Duplicate%'")
    long countDuplicateRejections();
}