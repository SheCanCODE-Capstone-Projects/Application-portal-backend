package com.igirerwanda.application_portal_backend.application.repository;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUserId(Long userId);
    List<Application> findByCohortId(Long cohortId);
    List<Application> findByStatus(ApplicationStatus status);
    Optional<Application> findByUserIdAndCohortId(Long userId, Long cohortId);
    long countByCohortIdAndStatus(Long cohortId, ApplicationStatus status);
    boolean existsByUserIdAndCohortId(Long userId, Long cohortId);
    
    @Query("SELECT a FROM Application a WHERE a.status = :status AND a.isSystemRejected = false")
    List<Application> findByStatusAndNotSystemRejected(ApplicationStatus status);
}
