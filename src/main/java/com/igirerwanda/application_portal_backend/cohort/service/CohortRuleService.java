package com.igirerwanda.application_portal_backend.cohort.service;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import org.springframework.stereotype.Service;

@Service
public class CohortRuleService {

    public boolean evaluateApplication(Application application, Cohort cohort) {
        if (cohort == null || cohort.getRequirements() == null) {
            return true;
        }

        for (String requirement : cohort.getRequirements()) {
            if (!evaluateRequirement(application, requirement)) {
                return false;
            }
        }
        return true;
    }

    public String getRejectionReason(Application application, Cohort cohort) {
        if (cohort == null || cohort.getRequirements() == null) {
            return null;
        }

        for (String requirement : cohort.getRequirements()) {
            if (!evaluateRequirement(application, requirement)) {
                return "Does not meet requirement: " + requirement;
            }
        }
        return null;
    }

    private boolean evaluateRequirement(Application application, String requirement) {
        // Basic rule evaluation - can be extended based on specific requirements
        return true; // Placeholder - implement specific rule logic here
    }
}


