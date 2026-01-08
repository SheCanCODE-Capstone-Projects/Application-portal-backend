package com.igirerwanda.application_portal_backend.application.repository;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByUserId(Long userId);
    List<Application> findByStatus(ApplicationStatus status);

    Optional<Application> findByUserIdAndCohortId(Long userId, Long cohortId);

}