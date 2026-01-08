package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import org.springframework.stereotype.Service;

@Service
public class ApplicationValidationService {

    public void validateForSubmission(Application application) {
        // Basic logic: Check if required fields are present
        if (application.getPersonalInformation() == null) {
            throw new IllegalStateException("Personal Information is required before submission.");
        }
        // Add more validation logic here (Education, Motivation, etc.)
    }
}