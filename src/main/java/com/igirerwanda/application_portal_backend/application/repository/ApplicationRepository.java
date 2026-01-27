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
}