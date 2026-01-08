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

    Optional<Application> findByUserId(Long userId);

    Optional<Application> findByUserIdAndCohortId(Long userId, Long cohortId);

    List<Application> findByStatusAndDeletedFalse(ApplicationStatus status);

    List<Application> findByIsSystemRejectedTrueAndDeletedFalse();

    List<Application> findByArchivedTrueAndDeletedFalse();

    List<Application> findByDeletedTrue();


    List<Application> findByDeletedFalseAndArchivedFalse();

    @Query("SELECT a FROM Application a WHERE a.id = :id AND a.deleted = false")
    Optional<Application> findByIdActive(Long id);
}