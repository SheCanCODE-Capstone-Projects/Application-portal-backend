package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.entity.PersonalInformation;
import com.igirerwanda.application_portal_backend.application.repository.DocumentRepository;
import com.igirerwanda.application_portal_backend.application.repository.EmergencyContactRepository;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.common.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationValidationService {

    private final DocumentRepository documentRepository;
    private final EmergencyContactRepository emergencyContactRepository;

    public void validateForSubmission(Application application) {
        if (application.getStatus() != ApplicationStatus.DRAFT) {
            throw new ValidationException("Only DRAFT applications can be submitted");
        }
        
        List<String> errors = new ArrayList<>();
        
        PersonalInformation pi = application.getPersonalInformation();
        if (pi == null) {
            errors.add("Personal Information is required");
        } else {
            validatePersonalInfo(pi, errors);
            validateEducation(pi, errors);
            validateMotivation(pi, errors);
            validateDocuments(pi, errors);
            validateEmergencyContacts(pi, errors);
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Application validation failed: " + String.join(", ", errors));
        }
    }
    
    private void validatePersonalInfo(PersonalInformation pi, List<String> errors) {
        if (!StringUtils.hasText(pi.getFullName())) {
            errors.add("Full name is required");
        }
        if (!StringUtils.hasText(pi.getEmail())) {
            errors.add("Email is required");
        }
        if (!StringUtils.hasText(pi.getPhone())) {
            errors.add("Phone number is required");
        }
    }
    
    private void validateEducation(PersonalInformation pi, List<String> errors) {
        if (pi.getEducationOccupation() == null) {
            errors.add("Education information is required");
        } else {
            if (pi.getEducationOccupation().getHighestEducation() == null) {
                errors.add("Highest education level is required");
            }
        }
    }
    
    private void validateMotivation(PersonalInformation pi, List<String> errors) {
        if (pi.getMotivationAnswer() == null) {
            errors.add("Motivation answers are required");
        } else {
            if (!StringUtils.hasText(pi.getMotivationAnswer().getWhyJoin())) {
                errors.add("Why join answer is required");
            }
            if (!StringUtils.hasText(pi.getMotivationAnswer().getFutureGoals())) {
                errors.add("Future goals answer is required");
            }
        }
    }
    
    private void validateDocuments(PersonalInformation pi, List<String> errors) {
        var documents = documentRepository.findByPersonalInformation(pi);
        if (documents == null || documents.isEmpty()) {
            errors.add("At least one document is required");
        }
    }
    
    private void validateEmergencyContacts(PersonalInformation pi, List<String> errors) {
        var contacts = emergencyContactRepository.findByPersonalInformation(pi);
        if (contacts == null || contacts.isEmpty()) {
            errors.add("At least one emergency contact is required");
        }
    }
}