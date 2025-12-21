package com.igirerwanda.application_portal_backend.application.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void personalInfoDto_ValidData_NoViolations() {
        PersonalInfoDto dto = new PersonalInfoDto();
        dto.setFullName("John Doe");
        dto.setEmail("john@example.com");
        dto.setPhone("+1234567890");

        Set<ConstraintViolation<PersonalInfoDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void personalInfoDto_InvalidEmail_HasViolations() {
        PersonalInfoDto dto = new PersonalInfoDto();
        dto.setFullName("John Doe");
        dto.setEmail("invalid-email");
        dto.setPhone("+1234567890");

        Set<ConstraintViolation<PersonalInfoDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void applicationCreateDto_NullCohortId_HasViolations() {
        ApplicationCreateDto dto = new ApplicationCreateDto();

        Set<ConstraintViolation<ApplicationCreateDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }
}