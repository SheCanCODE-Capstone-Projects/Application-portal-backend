package com.igirerwanda.application_portal_backend.cohort.repository;

import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CohortRepository extends JpaRepository<Cohort, Long> {
    Optional<Cohort> findFirstByIsOpenTrue();
    Optional<Cohort> findByName(String name);
    @EntityGraph(attributePaths = {"requirements", "rules"})
    List<Cohort> findAll();

    @EntityGraph(attributePaths = {"requirements", "rules"})
    Optional<Cohort> findById(Long id);

    List<Cohort> findByIsOpenTrue();
}
