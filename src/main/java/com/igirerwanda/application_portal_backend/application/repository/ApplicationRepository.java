package com.igirerwanda.application_portal_backend.application.repository;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    @Query("SELECT a FROM Application a WHERE a.user.id = :userId")
    List<Application> findByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT a FROM Application a WHERE a.cohort.id = :cohortId")
    List<Application> findByCohortId(@Param("cohortId") UUID cohortId);
    
    List<Application> findByStatus(ApplicationStatus status);
    
    @Query("SELECT a FROM Application a WHERE a.user.id = :userId AND a.cohort.id = :cohortId")
    Optional<Application> findByUserIdAndCohortId(@Param("userId") UUID userId, @Param("cohortId") UUID cohortId);
    
    @Query("SELECT COUNT(a) FROM Application a WHERE a.cohort.id = :cohortId AND a.status = :status")
    long countByCohortIdAndStatus(@Param("cohortId") UUID cohortId, @Param("status") ApplicationStatus status);
    
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Application a WHERE a.user.id = :userId AND a.cohort.id = :cohortId")
    boolean existsByUserIdAndCohortId(@Param("userId") UUID userId, @Param("cohortId") UUID cohortId);
    
    @Query("SELECT a FROM Application a WHERE a.status = :status AND a.isSystemRejected = false")
    List<Application> findByStatusAndNotSystemRejected(@Param("status") ApplicationStatus status);
}
