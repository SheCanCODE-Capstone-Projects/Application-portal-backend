package com.igirerwanda.application_portal_backend.cohort.repository;

import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CohortRepository extends JpaRepository<Cohort, Long> {
    Optional<Cohort> findFirstByIsOpenTrue();
    Optional<Cohort> findByName(String name);
}
