package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.*;
import com.igirerwanda.application_portal_backend.application.entity.*;
import com.igirerwanda.application_portal_backend.application.repository.*;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.cohort.service.CohortRuleService;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import com.igirerwanda.application_portal_backend.common.exception.ResourceNotFoundException;
import com.igirerwanda.application_portal_backend.common.exception.ValidationException;
import com.igirerwanda.application_portal_backend.me.service.MasterDataService;
import com.igirerwanda.application_portal_backend.notification.service.NotificationService;
import com.igirerwanda.application_portal_backend.notification.service.WebSocketService;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserApplicationServiceImpl implements UserApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserService userService;
    private final PersonalInfoRepository personalInfoRepository;
    private final EducationalRepository educationalRepository;
    private final MotivationAnswerRepository motivationAnswerRepository;
    private final DisabilityRepository disabilityRepository;
    private final VulnerabilityInformationRepository vulnerabilityRepository;
    private final DocumentRepository documentRepository;
    private final EmergencyContactRepository emergencyContactRepository;

    private final ApplicationValidationService applicationValidationService;
    private final SystemRejectionService systemRejectionService;
    private final WebSocketService webSocketService;

    private final CohortRuleService cohortRuleService;
    private final MasterDataService masterDataService;
    private final NotificationService notificationService;

    @Override
    public ApplicationDto startApplicationForUser(UUID registerId) {
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
                    Application savedApp = applicationRepository.save(newApp);

                    // 1. Real-time update for dashboard
                    webSocketService.broadcastApplicationUpdate(Map.of(
                            "event", "APPLICATION_STARTED",
                            "applicationId", savedApp.getId().toString(),
                            "userId", user.getId().toString(),
                            "status", ApplicationStatus.DRAFT
                    ));

                    // 2. NEW: Send Notification (Email + In-App)
                    notificationService.sendApplicationStartedNotification(savedApp);

                    return savedApp;
                });

        return mapToDto(app);
    }

    @Override
    public ApplicationDto savePersonalInfo(UUID appId, UUID registerId, PersonalInfoDto dto) {
        Application application = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(application);

        pi.setFullName(dto.getFullName());
        pi.setEmail(dto.getEmail());
        pi.setPhone(dto.getPhone());
        pi.setGender(dto.getGender());
        pi.setNationality(dto.getNationality());
        pi.setMaritalStatus(dto.getMaritalStatus());
        pi.setSocialLinks(dto.getSocialLinks());
        pi.setAdditionalInformation(dto.getAdditionalInformation());

        personalInfoRepository.save(pi);
        broadcastStepUpdate(application, "PERSONAL_INFO");

        return mapToDto(application);
    }

    @Override
    public ApplicationDto saveEducation(UUID appId, UUID registerId, EducationDto dto) {
        Application application = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(application);

        EducationOccupation edu = getOrCreate(pi::getEducationOccupation, EducationOccupation::new);
        edu.setPersonalInformation(pi);
        edu.setHighestEducationLevel(dto.getHighestEducationLevel());
        edu.setHighestEducation(dto.getHighestEducation());
        edu.setOccupation(dto.getOccupation());
        edu.setEmploymentStatus(dto.getEmploymentStatus());
        edu.setYearsExperience(dto.getYearsExperience());

        educationalRepository.save(edu);
        broadcastStepUpdate(application, "EDUCATION");

        return mapToDto(application);
    }

    @Override
    public ApplicationDto saveMotivation(UUID appId, UUID registerId, MotivationDto dto) {
        Application application = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(application);

        MotivationAnswer m = getOrCreate(pi::getMotivationAnswer, MotivationAnswer::new);
        m.setPersonalInformation(pi);
        m.setWhyJoin(dto.getWhyJoin());
        m.setFutureGoals(dto.getFutureGoals());
        m.setPreferredCourse(dto.getPreferredCourse());

        motivationAnswerRepository.save(m);
        broadcastStepUpdate(application, "MOTIVATION");

        return mapToDto(application);
    }

    @Override
    public ApplicationDto saveDisability(UUID appId, UUID registerId, DisabilityDto dto) {
        Application application = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(application);

        DisabilityInformation d = getOrCreate(pi::getDisabilityInformation, DisabilityInformation::new);
        d.setPersonalInformation(pi);
        d.setHasDisability(dto.getHasDisability());
        d.setDisabilityType(dto.getDisabilityType());
        d.setDisabilityDescription(dto.getDisabilityDescription());

        disabilityRepository.save(d);
        broadcastStepUpdate(application, "DISABILITY");

        return mapToDto(application);
    }

    @Override
    public ApplicationDto saveVulnerability(UUID appId, UUID registerId, VulnerabilityDto dto) {
        Application application = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(application);

        VulnerabilityInformation v = getOrCreate(pi::getVulnerabilityInformation, VulnerabilityInformation::new);
        v.setPersonalInformation(pi);
        v.setHouseholdIncome(dto.getHouseholdIncome());
        v.setHasChildcareNeeds(dto.getHasChildcareNeeds());
        v.setDescription(dto.getDescription());

        vulnerabilityRepository.save(v);
        broadcastStepUpdate(application, "VULNERABILITY");

        return mapToDto(application);
    }

    @Override
    public ApplicationDto saveDocuments(UUID appId, UUID registerId, List<DocumentDto> documentDtos) {
        Application application = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(application);

        documentRepository.deleteByPersonalInformation(pi);
        documentRepository.flush();

        List<Document> documents = documentDtos.stream().map(dto -> {
            Document doc = new Document();
            doc.setPersonalInformation(pi);
            doc.setDocType(dto.getDocType());
            doc.setFileUrl(dto.getFileUrl());
            return doc;
        }).collect(Collectors.toList());

        documentRepository.saveAll(documents);
        broadcastStepUpdate(application, "DOCUMENTS");

        return mapToDto(application);
    }

    @Override
    public ApplicationDto saveEmergencyContacts(UUID appId, UUID registerId, List<EmergencyContactDto> contactDtos) {
        Application application = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(application);

        emergencyContactRepository.deleteByPersonalInformation(pi);
        emergencyContactRepository.flush();

        List<EmergencyContact> contacts = contactDtos.stream().map(dto -> {
            EmergencyContact contact = new EmergencyContact();
            contact.setPersonalInformation(pi);
            contact.setName(dto.getName());
            contact.setRelationship(dto.getRelationship());
            contact.setPhone(dto.getPhone());
            return contact;
        }).collect(Collectors.toList());

        emergencyContactRepository.saveAll(contacts);
        broadcastStepUpdate(application, "EMERGENCY_CONTACTS");

        return mapToDto(application);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationDto getApplicationForUser(UUID registerId) {
        User user = userService.findByRegisterId(registerId);

        Application application = applicationRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No application found for user"));

        return mapToDto(application);
    }

    @Override
    public ApplicationSubmissionResponseDto submitApplication(UUID appId, UUID registerId) {
        Application app = getOwnedApplication(appId, registerId);

        // 1. Validation
        applicationValidationService.validateForSubmission(app);

        // 2. GATE 1: Demographic & Education Rules
        String rejectionReason = cohortRuleService.evaluateApplication(app, app.getCohort());
        if (rejectionReason != null) {
            return rejectApplication(app, rejectionReason);
        }

        // 3. GATE 2: Master Data Synchronization Check
        if (masterDataService.isUserInMasterDatabase(app.getPersonalInformation())) {
            return rejectApplication(app, "Duplicate Entry: Applicant exists in Master Database.");
        }

        // 4. Success Path
        app.setStatus(ApplicationStatus.SUBMITTED);
        app.setSubmittedAt(LocalDateTime.now());
        app.setSystemRejected(false);
        app.setSystemRejectionReason(null);

        Application savedApp = applicationRepository.save(app);

        // Notify
        notificationService.sendApplicationSubmittedNotification(savedApp);

        return ApplicationSubmissionResponseDto.submitted(mapToDto(savedApp));
    }

    private ApplicationSubmissionResponseDto rejectApplication(Application app, String reason) {
        app.setStatus(ApplicationStatus.SYSTEM_REJECTED);
        app.setSystemRejected(true);
        app.setSystemRejectionReason(reason);
        app.setSubmittedAt(LocalDateTime.now());

        Application saved = applicationRepository.save(app);

        ApplicationDto appDto = mapToDto(saved);

        return new ApplicationSubmissionResponseDto(
                ApplicationStatus.SYSTEM_REJECTED,
                "Your application is under review.",
                appDto
        );
    }

    @Override
    public double calculateCompletionPercentage(UUID applicationId, UUID userId) { // userId here is actually registerId from token
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found"));

        // FIX: Compare with the Register ID (from token) instead of User ID
        if (!app.getUser().getRegister().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied: You do not own this application.");
        }

        int score = 0;
        int totalSections = 7;

        PersonalInformation pi = app.getPersonalInformation();
        if (pi != null) {
            score++;
            if (pi.getEducationOccupation() != null) score++;
            if (pi.getMotivationAnswer() != null) score++;
            if (pi.getDisabilityInformation() != null) score++;
            if (pi.getVulnerabilityInformation() != null) score++;

            List<Document> docs = documentRepository.findByPersonalInformation(pi);
            if (docs != null && !docs.isEmpty()) score++;

            List<EmergencyContact> contacts = emergencyContactRepository.findByPersonalInformation(pi);
            if (contacts != null && !contacts.isEmpty()) score++;
        }

        return ((double) score / totalSections) * 100;
    }

    private Application getOwnedApplication(UUID appId, UUID registerId) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new NotFoundException("Application not found"));

        if (!app.getUser().getRegister().getId().equals(registerId)) {
            throw new AccessDeniedException("Access denied: You do not own this application.");
        }

        if (app.getStatus() != ApplicationStatus.DRAFT) {
            throw new ValidationException("Cannot edit application with status: " + app.getStatus() + ". Only DRAFT applications can be edited.");
        }

        return app;
    }

    private PersonalInformation ensurePersonalInfo(Application app) {
        if (app.getPersonalInformation() == null) {
            PersonalInformation pi = new PersonalInformation();
            pi.setApplication(app);
            app.setPersonalInformation(pi);
            return personalInfoRepository.save(pi);
        }
        return app.getPersonalInformation();
    }

    private <T> T getOrCreate(java.util.function.Supplier<T> getter, java.util.function.Supplier<T> constructor) {
        T value = getter.get();
        return (value != null) ? value : constructor.get();
    }

    private void broadcastStepUpdate(Application application, String stepName) {
        double progress = calculateCompletionPercentage(application.getId(), application.getUser().getRegister().getId());

        webSocketService.broadcastApplicationProgress(
                application.getUser().getRegister().getId().toString(), // Use Register ID for consistency if needed, or stick to User ID if frontend listens to that
                Map.of(
                        "event", "STEP_UPDATED",
                        "applicationId", application.getId().toString(),
                        "step", stepName,
                        "progress", progress
                )
        );
        // ... rest of method
    }

    private ApplicationDto mapToDto(Application app) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(app.getId());
        dto.setUserId(app.getUser().getId());
        dto.setStatus(app.getStatus());
        dto.setSystemRejected(app.isSystemRejected());
        dto.setSystemRejectionReason(app.getSystemRejectionReason());

        if (app.getCohort() != null) {
            dto.setCohortId(app.getCohort().getId());
            dto.setCohortName(app.getCohort().getName());
        }

        dto.setSubmittedAt(app.getSubmittedAt());
        dto.setCreatedAt(app.getCreatedAt());

        PersonalInformation pi = app.getPersonalInformation();
        if (pi != null) {
            PersonalInfoDto piDto = new PersonalInfoDto();
            piDto.setFullName(pi.getFullName());
            piDto.setEmail(pi.getEmail());
            piDto.setPhone(pi.getPhone());
            piDto.setGender(pi.getGender());
            piDto.setNationality(pi.getNationality());
            piDto.setMaritalStatus(pi.getMaritalStatus());
            piDto.setSocialLinks(pi.getSocialLinks());
            piDto.setAdditionalInformation(pi.getAdditionalInformation());
            dto.setPersonalInfo(piDto);

            if (pi.getEducationOccupation() != null) {
                EducationOccupation edu = pi.getEducationOccupation();
                EducationDto eduDto = new EducationDto();
                eduDto.setHighestEducationLevel(edu.getHighestEducationLevel());
                eduDto.setHighestEducation(edu.getHighestEducation());
                eduDto.setOccupation(edu.getOccupation());
                eduDto.setEmploymentStatus(edu.getEmploymentStatus());
                eduDto.setYearsExperience(edu.getYearsExperience());
                dto.setEducation(eduDto);
            }

            if (pi.getMotivationAnswer() != null) {
                MotivationAnswer m = pi.getMotivationAnswer();
                MotivationDto mDto = new MotivationDto();
                mDto.setWhyJoin(m.getWhyJoin());
                mDto.setFutureGoals(m.getFutureGoals());
                mDto.setPreferredCourse(m.getPreferredCourse());
                dto.setMotivation(mDto);
            }

            if (pi.getDisabilityInformation() != null) {
                DisabilityInformation d = pi.getDisabilityInformation();
                DisabilityDto dDto = new DisabilityDto();
                dDto.setHasDisability(d.getHasDisability());
                dDto.setDisabilityType(d.getDisabilityType());
                dDto.setDisabilityDescription(d.getDisabilityDescription());
                dto.setDisability(dDto);
            }

            if (pi.getVulnerabilityInformation() != null) {
                VulnerabilityInformation v = pi.getVulnerabilityInformation();
                VulnerabilityDto vDto = new VulnerabilityDto();
                vDto.setHouseholdIncome(v.getHouseholdIncome());
                vDto.setHasChildcareNeeds(v.getHasChildcareNeeds());
                vDto.setDescription(v.getDescription());
                dto.setVulnerability(vDto);
            }

            List<Document> docs = documentRepository.findByPersonalInformation(pi);
            if (docs != null) {
                List<DocumentDto> docDtos = docs.stream().map(doc -> {
                    DocumentDto dDto = new DocumentDto();
                    dDto.setDocType(doc.getDocType());
                    dDto.setFileUrl(doc.getFileUrl());
                    return dDto;
                }).collect(Collectors.toList());
                dto.setDocuments(docDtos);
            }

            List<EmergencyContact> contacts = emergencyContactRepository.findByPersonalInformation(pi);
            if (contacts != null) {
                List<EmergencyContactDto> contactDtos = contacts.stream().map(c -> {
                    EmergencyContactDto cDto = new EmergencyContactDto();
                    cDto.setName(c.getName());
                    cDto.setRelationship(c.getRelationship());
                    cDto.setPhone(c.getPhone());
                    return cDto;
                }).collect(Collectors.toList());
                dto.setEmergencyContacts(contactDtos);
            }
        }
        return dto;
    }
}