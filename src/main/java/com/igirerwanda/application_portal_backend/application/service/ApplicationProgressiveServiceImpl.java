package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.*;
import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationProgressiveServiceImpl implements ApplicationProgressiveService {

    private final ApplicationService applicationService;
    private final ApplicationRepository applicationRepository;

    @Override
    public ApplicationDto savePersonalInfoStep(Long applicationId, PersonalInfoDto dto) {
        return applicationService.updatePersonalInfo(applicationId, dto);
    }

    @Override
    public ApplicationDto saveEducationStep(Long applicationId, EducationDto dto) {
        return applicationService.updateEducation(applicationId, dto);
    }

    @Override
    public ApplicationDto saveMotivationStep(Long applicationId, MotivationDto dto) {
        return applicationService.updateMotivation(applicationId, dto);
    }

    @Override
    public ApplicationDto saveDisabilityStep(Long applicationId, DisabilityDto dto) {
        return applicationService.updateDisability(applicationId, dto);
    }

    @Override
    public ApplicationDto saveVulnerabilityStep(Long applicationId, VulnerabilityDto dto) {
        return applicationService.updateVulnerability(applicationId, dto);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationDto getApplicationProgress(Long applicationId) {
        return applicationService.getApplication(applicationId);
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateCompletionPercentage(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found"));
        
        int totalSteps = 5; // Personal, Education, Motivation, Disability, Vulnerability
        int completedSteps = 0;
        
        if (application.getPersonalInformation() != null) {
            completedSteps++;
            
            if (application.getPersonalInformation().getEducationOccupation() != null) {
                completedSteps++;
            }
            
            if (application.getPersonalInformation().getMotivationAnswer() != null) {
                completedSteps++;
            }
            
            if (application.getPersonalInformation().getDisabilityInformation() != null) {
                completedSteps++;
            }
            
            if (application.getPersonalInformation().getVulnerabilityInformation() != null) {
                completedSteps++;
            }
        }
        
        return (double) completedSteps / totalSteps * 100;
    }
}
