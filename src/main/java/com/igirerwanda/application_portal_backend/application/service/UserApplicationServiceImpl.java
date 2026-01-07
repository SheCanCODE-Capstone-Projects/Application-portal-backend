package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.*;
import com.igirerwanda.application_portal_backend.application.entity.*;
import com.igirerwanda.application_portal_backend.application.repository.*;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import com.igirerwanda.application_portal_backend.common.exception.ValidationException;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class UserApplicationServiceImpl implements UserApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserService userService;
    private final PersonalInfoRepository personalInfoRepository;
    private final EducationalRepository educationalRepository;
    private final MotivationAnswerRepository motivationAnswerRepository;
    private final DisabilityRepository disabilityRepository;
    private final VulnerabilityInformationRepository vulnerabilityRepository;

    @Override
    public ApplicationDto startApplicationForUser(Long registerId) { // registerId comes from token
        // ✅ FIX: Use findByRegisterId because token has Register ID
        User user = userService.findByRegisterId(registerId);
        Cohort cohort = user.getCohort();

        if (cohort == null) {
            throw new IllegalStateException("Please select a cohort before starting an application.");
        }

        Application app = applicationRepository.findByUserIdAndCohortId(user.getId(), cohort.getId())
                .orElseGet(() -> {
                    Application newApp = new Application();
                    newApp.setUser(user);
                    newApp.setCohort(cohort);
                    newApp.setStatus(ApplicationStatus.DRAFT);
                    return applicationRepository.save(newApp);
                });

        return mapToDto(app);
    }

    @Override
    public ApplicationDto savePersonalInfo(Long appId, Long registerId, PersonalInfoDto dto) {
        Application application = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(application);

        pi.setFullName(dto.getFullName());
        pi.setEmail(dto.getEmail());
        pi.setPhone(dto.getPhone());
        pi.setMaritalStatus(dto.getMaritalStatus());
        pi.setSocialLinks(dto.getSocialLinks());
        pi.setAdditionalInformation(dto.getAdditionalInformation());

        personalInfoRepository.save(pi);
        return mapToDto(application);
    }

    @Override
    public ApplicationDto saveEducation(Long appId, Long registerId, EducationDto dto) {
        Application application = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(application);

        EducationOccupation edu = (pi.getEducationOccupation() != null)
                ? pi.getEducationOccupation() : new EducationOccupation();

        edu.setPersonalInformation(pi);
        edu.setHighestEducation(dto.getHighestEducation());
        edu.setOccupation(dto.getOccupation());
        edu.setEmploymentStatus(dto.getEmploymentStatus());
        edu.setYearsExperience(dto.getYearsExperience());

        educationalRepository.save(edu);
        return mapToDto(application);
    }

    @Override
    public ApplicationDto saveMotivation(Long appId, Long registerId, MotivationDto dto) {
        Application app = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(app);

        MotivationAnswer m = (pi.getMotivationAnswer() != null)
                ? pi.getMotivationAnswer() : new MotivationAnswer();

        m.setPersonalInformation(pi);
        m.setWhyJoin(dto.getWhyJoin());
        m.setFutureGoals(dto.getFutureGoals());
        m.setPreferredCourse(dto.getPreferredCourse());

        motivationAnswerRepository.save(m);
        return mapToDto(app);
    }

    @Override
    public ApplicationDto saveDisability(Long appId, Long registerId, DisabilityDto dto) {
        Application app = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(app);

        DisabilityInformation d = (pi.getDisabilityInformation() != null)
                ? pi.getDisabilityInformation() : new DisabilityInformation();

        d.setPersonalInformation(pi);
        d.setHasDisability(dto.getHasDisability());
        d.setDisabilityType(dto.getDisabilityType());
        d.setDisabilityDescription(dto.getDisabilityDescription());

        disabilityRepository.save(d);
        return mapToDto(app);
    }

    @Override
    public ApplicationDto saveVulnerability(Long appId, Long registerId, VulnerabilityDto dto) {
        Application app = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(app);

        VulnerabilityInformation v = (pi.getVulnerabilityInformation() != null)
                ? pi.getVulnerabilityInformation() : new VulnerabilityInformation();

        v.setPersonalInformation(pi);
        v.setHouseholdIncome(dto.getHouseholdIncome());
        v.setHasChildcareNeeds(dto.getHasChildcareNeeds());
        v.setDescription(dto.getDescription());

        vulnerabilityRepository.save(v);
        return mapToDto(app);
    }

    @Override
    public ApplicationDto submitApplication(Long appId, Long registerId) {
        Application app = getOwnedApplication(appId, registerId);

        if (app.getPersonalInformation() == null) {
            throw new ValidationException("Please complete your personal information before submitting.");
        }

        app.setStatus(ApplicationStatus.SUBMITTED);
        app.setSubmittedAt(LocalDateTime.now());
        return mapToDto(applicationRepository.save(app));
    }

    @Override
    public double calculateCompletionPercentage(Long applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found"));

        int score = 0;
        int totalSections = 5;

        if (app.getPersonalInformation() != null) {
            score++;
            if (app.getPersonalInformation().getEducationOccupation() != null) score++;
            if (app.getPersonalInformation().getMotivationAnswer() != null) score++;
            if (app.getPersonalInformation().getDisabilityInformation() != null) score++;
            if (app.getPersonalInformation().getVulnerabilityInformation() != null) score++;
        }

        return ((double) score / totalSections) * 100;
    }

    private Application getOwnedApplication(Long appId, Long registerId) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new NotFoundException("Application not found"));

        // ✅ FIX: Compare Token ID (Register ID) with the App -> User -> Register ID
        if (!app.getUser().getRegister().getId().equals(registerId)) {
            throw new SecurityException("Access denied: You do not own this application.");
        }
        return app;
    }

    private PersonalInformation ensurePersonalInfo(Application app) {
        if (app.getPersonalInformation() == null) {
            PersonalInformation pi = new PersonalInformation();
            pi.setApplication(app);
            return personalInfoRepository.save(pi);
        }
        return app.getPersonalInformation();
    }

    private ApplicationDto mapToDto(Application app) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(app.getId());
        dto.setStatus(app.getStatus());
        dto.setCohortName(app.getCohort() != null ? app.getCohort().getName() : null);
        dto.setSubmittedAt(app.getSubmittedAt());
        // Add other mapping logic here if needed
        return dto;
    }
}