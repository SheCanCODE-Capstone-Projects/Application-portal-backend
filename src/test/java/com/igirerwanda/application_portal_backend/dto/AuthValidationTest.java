package com.igirerwanda.application_portal_backend.dto;

import com.igirerwanda.application_portal_backend.auth.dto.LoginRequest;
import com.igirerwanda.application_portal_backend.auth.dto.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void registerRequest_Valid_ShouldPass() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setUsername("testuser");
        request.setPassword("StrongPass123!");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void registerRequest_InvalidEmail_ShouldFail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("invalid-email");
        request.setUsername("user");
        request.setPassword("pass");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void loginRequest_BlankFields_ShouldFail() {
        LoginRequest request = new LoginRequest("", "");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }
}