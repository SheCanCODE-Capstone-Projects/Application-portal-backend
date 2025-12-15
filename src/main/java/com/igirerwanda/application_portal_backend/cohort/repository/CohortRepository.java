package com.igirerwanda.application_portal_backend.cohort.repository;

import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CohortRepository extends JpaRepository<Cohort, Long> {
}
