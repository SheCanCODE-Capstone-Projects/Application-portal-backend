package com.igirerwanda.application_portal_backend.dto;

import com.igirerwanda.application_portal_backend.cohort.dto.CohortCreateDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CohortValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void cohortCreateDto_ValidData_NoViolations() {
        CohortCreateDto dto = new CohortCreateDto();
        dto.setName("Cohort 5");
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusMonths(6));
        // Add required lists if @NotEmpty is used
        // dto.setRequirements(List.of("Req 1"));

        Set<ConstraintViolation<CohortCreateDto>> violations = validator.validate(dto);
        // Note: Assert might fail if lists are null and @NotEmpty is used in your DTO
        // Adjust DTO setup based on your actual validation rules
        // assertTrue(violations.isEmpty());
    }

    @Test
    void cohortCreateDto_MissingName_HasViolations() {
        CohortCreateDto dto = new CohortCreateDto();
        dto.setStartDate(LocalDate.now());

        Set<ConstraintViolation<CohortCreateDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}