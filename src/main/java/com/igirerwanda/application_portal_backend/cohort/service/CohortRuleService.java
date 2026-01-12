package com.igirerwanda.application_portal_backend.cohort.service;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.entity.PersonalInformation;
import com.igirerwanda.application_portal_backend.application.entity.EducationOccupation;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.common.enums.Gender;
import com.igirerwanda.application_portal_backend.common.enums.EducationalLevel;
import org.springframework.stereotype.Service;

@Service
public class CohortRuleService {

    public boolean evaluateApplication(Application application, Cohort cohort) {
        if (cohort == null || application.getPersonalInformation() == null) {
            return true;
        }

        PersonalInformation personalInfo = application.getPersonalInformation();
        
        // Evaluate gender restrictions
        if (!evaluateGenderEligibility(personalInfo, cohort)) {
            return false;
        }
        
        // Evaluate nationality restrictions
        if (!evaluateNationalityEligibility(personalInfo, cohort)) {
            return false;
        }
        
        // Evaluate education level requirements
        if (!evaluateEducationEligibility(personalInfo, cohort)) {
            return false;
        }
        
        return true;
    }

    public String getRejectionReason(Application application, Cohort cohort) {
        if (cohort == null || application.getPersonalInformation() == null) {
            return null;
        }

        PersonalInformation personalInfo = application.getPersonalInformation();
        
        // Check gender restrictions
        if (!evaluateGenderEligibility(personalInfo, cohort)) {
            return "Gender does not meet cohort requirements";
        }
        
        // Check nationality restrictions
        if (!evaluateNationalityEligibility(personalInfo, cohort)) {
            return "Nationality is not allowed for this cohort";
        }
        
        // Check education level requirements
        if (!evaluateEducationEligibility(personalInfo, cohort)) {
            return "Education level does not meet cohort requirements";
        }
        
        return null;
    }
    
    private boolean evaluateGenderEligibility(PersonalInformation personalInfo, Cohort cohort) {
        // If no gender restrictions are set, allow all genders
        if (cohort.getAllowedGenders() == null || cohort.getAllowedGenders().isEmpty()) {
            return true;
        }
        
        // If applicant's gender is null, reject
        if (personalInfo.getGender() == null) {
            return false;
        }
        
        // Check if applicant's gender is in allowed genders
        return cohort.getAllowedGenders().contains(personalInfo.getGender());
    }
    
    private boolean evaluateNationalityEligibility(PersonalInformation personalInfo, Cohort cohort) {
        // If no nationality restrictions are set, allow all nationalities
        if (cohort.getAllowedNationalities() == null || cohort.getAllowedNationalities().isEmpty()) {
            return true;
        }
        
        // If applicant's nationality is null or empty, reject
        if (personalInfo.getNationality() == null || personalInfo.getNationality().trim().isEmpty()) {
            return false;
        }
        
        // Check if applicant's nationality is in allowed nationalities (case-insensitive)
        return cohort.getAllowedNationalities().stream()
                .anyMatch(allowedNationality -> 
                    allowedNationality.equalsIgnoreCase(personalInfo.getNationality().trim()));
    }
    
    private boolean evaluateEducationEligibility(PersonalInformation personalInfo, Cohort cohort) {
        // If no education level requirements are set, allow all education levels
        if (cohort.getRequiredEducationLevels() == null || cohort.getRequiredEducationLevels().isEmpty()) {
            return true;
        }
        
        // If applicant has no education information, reject
        EducationOccupation education = personalInfo.getEducationOccupation();
        if (education == null || education.getHighestEducationLevel() == null) {
            return false;
        }
        
        // Check if applicant's education level meets the minimum requirements
        return cohort.getRequiredEducationLevels().contains(education.getHighestEducationLevel());
    }
}