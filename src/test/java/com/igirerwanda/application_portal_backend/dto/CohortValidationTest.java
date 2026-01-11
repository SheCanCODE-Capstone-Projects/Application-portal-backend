package com.igirerwanda.application_portal_backend.dto;

import com.igirerwanda.application_portal_backend.cohort.dto.CohortCreateDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CohortValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void cohortCreateDto_Valid_ShouldPass() {
        CohortCreateDto dto = new CohortCreateDto();
        dto.setName("Cohort 1");
        dto.setDescription("Test Description");
        dto.setStartDate(LocalDate.now().plusDays(1));
        dto.setEndDate(LocalDate.now().plusMonths(3));
        dto.setApplicationLimit(50);

        Set<ConstraintViolation<CohortCreateDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void cohortCreateDto_NullName_ShouldFail() {
        CohortCreateDto dto = new CohortCreateDto();
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusMonths(1));

        Set<ConstraintViolation<CohortCreateDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}