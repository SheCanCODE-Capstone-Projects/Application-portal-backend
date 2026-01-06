package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.entity.PersonalInformation;
import com.igirerwanda.application_portal_backend.common.exception.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationValidationService {

    public void validateForSubmission(Application application) {
        if (application.getPersonalInformation() == null) {
            throw new ValidationException("Personal information is required for submission");
        }
        
        PersonalInformation personalInfo = application.getPersonalInformation();
        
        if (personalInfo.getFullName() == null || personalInfo.getFullName().trim().isEmpty()) {
            throw new ValidationException("Full name is required");
        }
        
        if (personalInfo.getEmail() == null || personalInfo.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email is required");
        }
        
        if (personalInfo.getPhone() == null || personalInfo.getPhone().trim().isEmpty()) {
            throw new ValidationException("Phone number is required");
        }
        
        if (personalInfo.getEducationOccupation() == null) {
            throw new ValidationException("Education information is required");
        }
        
        if (personalInfo.getMotivationAnswer() == null) {
            throw new ValidationException("Motivation answers are required");
        }
    }
    
    public boolean isApplicationComplete(Application application) {
        try {
            validateForSubmission(application);
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }
}
