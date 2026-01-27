package com.igirerwanda.application_portal_backend.cohort.repository;

import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CohortRepository extends JpaRepository<Cohort, UUID> {
    Optional<Cohort> findFirstByIsOpenTrue();
    Optional<Cohort> findByName(String name);

    @EntityGraph(attributePaths = {"requirements", "rules"})
    List<Cohort> findAll();

    @EntityGraph(attributePaths = {"requirements", "rules"})
    Optional<Cohort> findById(UUID id);

    List<Cohort> findByIsOpenTrue();
}