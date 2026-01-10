package com.igirerwanda.application_portal_backend.repository;

import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CohortRepositoryTest {

    @Autowired
    private CohortRepository cohortRepository;

    @Test
    void findByIsOpenTrue_ShouldReturnOpenCohorts() {
        Cohort openCohort = new Cohort();
        openCohort.setName("Open");
        openCohort.setIsOpen(true);
        cohortRepository.save(openCohort);

        Cohort closedCohort = new Cohort();
        closedCohort.setName("Closed");
        closedCohort.setIsOpen(false);
        cohortRepository.save(closedCohort);

        List<Cohort> openCohorts = cohortRepository.findByIsOpenTrue();

        assertEquals(1, openCohorts.size());
        assertEquals("Open", openCohorts.get(0).getName());
    }
}