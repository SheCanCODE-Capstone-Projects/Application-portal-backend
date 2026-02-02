package com.igirerwanda.application_portal_backend.cohort.service;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.entity.PersonalInformation;
import com.igirerwanda.application_portal_backend.application.entity.EducationOccupation;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.common.enums.Gender;
import com.igirerwanda.application_portal_backend.common.enums.EducationalLevel;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CohortRuleService {

    public String evaluateApplication(Application application, Cohort cohort) {
        PersonalInformation pi = application.getPersonalInformation();
        if (pi == null) return "Missing personal information.";

        // 1. Nationality: "if not a rwandan you are rejected"
        String nat = pi.getNationality();
        if (nat == null || (!nat.equalsIgnoreCase("Rwandan") && !nat.equalsIgnoreCase("Rwanda"))) {
            return "REJECT: Only Rwandan nationals are eligible.";
        }

        // 2. Gender: "if you are a boy... rejected" (Only Females allowed)
        if (pi.getGender() == Gender.MALE) {
            return "REJECT: This program is reserved for female applicants.";
        }

        // 3. Education: "university graduate or in their year"
        EducationOccupation edu = pi.getEducationOccupation();
        if (edu == null) return "Missing education information.";

        EducationalLevel level = edu.getHighestEducationLevel();
        // Assuming Bachelor/Master/PhD are graduates
        boolean isGraduate = List.of(EducationalLevel.BACHELOR, EducationalLevel.MASTER, EducationalLevel.PHD).contains(level);

        // Check "in their year" (Final year student)
        boolean isFinalYear = edu.getOccupation() != null &&
                (edu.getOccupation().toLowerCase().contains("student") ||
                        edu.getEmploymentStatus().toLowerCase().contains("student"));

        if (!isGraduate && !isFinalYear) {
            return "REJECT: Must be a university graduate or final year student.";
        }

        // 4. Coding Basics: "if you dont have basic in coding"
        // Checking for keywords in their education/occupation description or specific field
        // Assuming 'highestEducation' field might contain course details
        String background = (edu.getHighestEducation() + " " + edu.getOccupation()).toLowerCase();
        boolean hasCodingBasics = background.contains("computer") ||
                background.contains("software") ||
                background.contains("it") ||
                background.contains("code") ||
                background.contains("tech");

        if (!hasCodingBasics && (edu.getYearsExperience() == null || edu.getYearsExperience() < 1)) {
            return "REJECT: Basic coding knowledge or IT background is required.";
        }

        return null; // Passed Gate 1
    }
}