package com.igirerwanda.application_portal_backend.cohort.repository;

import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;

@Repository
public interface CohortRepository extends JpaRepository<Cohort, Long> {
    <T> ScopedValue<T> findFirstByIsOpenTrue();
}
