package com.igirerwanda.application_portal_backend.application.repository;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    Optional<Application> findByUserId(UUID userId);

    Optional<Application> findByUserIdAndCohortId(UUID userId, UUID cohortId);

    List<Application> findByStatusAndDeletedFalse(ApplicationStatus status);

    List<Application> findByIsSystemRejectedTrueAndDeletedFalse();

    List<Application> findByArchivedTrueAndDeletedFalse();

    List<Application> findByDeletedTrue();

    List<Application> findByDeletedFalseAndArchivedFalse();

    @Query("SELECT a FROM Application a WHERE a.id = :id AND a.deleted = false")
    Optional<Application> findByIdActive(UUID id);

    Object countByStatus(ApplicationStatus applicationStatus);


    // 1. Applications by Day (for the Graph)
    @Query(value = "SELECT DATE(created_at) as date, COUNT(*) as count FROM applications GROUP BY DATE(created_at) ORDER BY date ASC", nativeQuery = true)
    List<Object[]> countApplicationsByDay();

    // 2. Status breakdown by Cohort
    @Query("SELECT c.name, a.status, COUNT(a) FROM Application a JOIN a.cohort c GROUP BY c.name, a.status")
    List<Object[]> countApplicationsByCohortAndStatus();

    // 3. Sync/Duplicate Rejections (Applied Twice)
    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = 'SYSTEM_REJECTED' AND a.systemRejectionReason LIKE '%Duplicate%'")
    long countDuplicateRejections();

    // 4. Total System Rejects (General)
    long countByIsSystemRejectedTrue();
}