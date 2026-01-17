package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.*;
import com.igirerwanda.application_portal_backend.application.entity.*;
import com.igirerwanda.application_portal_backend.application.repository.*;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import com.igirerwanda.application_portal_backend.common.exception.ResourceNotFoundException;
import com.igirerwanda.application_portal_backend.common.exception.ValidationException;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException; // Required import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.igirerwanda.application_portal_backend.notification.service.WebSocketService;

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
    private final DocumentRepository documentRepository;
    private final EmergencyContactRepository emergencyContactRepository;

    // Injected validation service
    private final ApplicationValidationService applicationValidationService;
    private final WebSocketService webSocketService;

    @Override
    public ApplicationDto startApplicationForUser(Long registerId) {
        User user = userService.findByRegisterId(registerId);
        Cohort cohort = user.getCohort();

        if (cohort == null) {
            throw new IllegalStateException("Please select a cohort before starting an application.");
        }

        // Single active application per user - return existing DRAFT if found
        Application app = applicationRepository.findByUserIdAndCohortId(user.getId(), cohort.getId())
                .orElseGet(() -> {
                    Application newApp = new Application();
                    newApp.setUser(user);
                    newApp.setCohort(cohort);
                    newApp.setStatus(ApplicationStatus.DRAFT);
                    Application savedApp = applicationRepository.save(newApp);
                    
                    // Broadcast application started event
                    webSocketService.broadcastApplicationUpdate(Map.of(
                        "event", "APPLICATION_STARTED",
                        "applicationId", savedApp.getId(),
                        "userId", user.getId(),
                        "status", ApplicationStatus.DRAFT
                    ));
                    
                    return savedApp;
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
        
        // Broadcast step update event
        broadcastStepUpdate(application, "PERSONAL_INFO");
        
        return mapToDto(application);
    }

    @Override
    public ApplicationDto saveEducation(Long appId, Long registerId, EducationDto dto) {
        Application application = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(application);

        EducationOccupation edu = getOrCreate(pi::getEducationOccupation, EducationOccupation::new);
        edu.setPersonalInformation(pi);
        edu.setHighestEducation(dto.getHighestEducation());
        edu.setOccupation(dto.getOccupation());
        edu.setEmploymentStatus(dto.getEmploymentStatus());
        edu.setYearsExperience(dto.getYearsExperience());

        educationalRepository.save(edu);
        
        // Broadcast step update event
        broadcastStepUpdate(application, "EDUCATION");
        
        return mapToDto(application);
    }

    @Override
    public ApplicationDto saveMotivation(Long appId, Long registerId, MotivationDto dto) {
        Application application = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(application);

        MotivationAnswer m = getOrCreate(pi::getMotivationAnswer, MotivationAnswer::new);
        m.setPersonalInformation(pi);
        m.setWhyJoin(dto.getWhyJoin());
        m.setFutureGoals(dto.getFutureGoals());
        m.setPreferredCourse(dto.getPreferredCourse());

        motivationAnswerRepository.save(m);
        
        // Broadcast step update event
        broadcastStepUpdate(application, "MOTIVATION");
        
        return mapToDto(application);
    }

    @Override
    public ApplicationDto saveDisability(Long appId, Long registerId, DisabilityDto dto) {
        Application application = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(application);

        DisabilityInformation d = getOrCreate(pi::getDisabilityInformation, DisabilityInformation::new);
        d.setPersonalInformation(pi);
        d.setHasDisability(dto.getHasDisability());
        d.setDisabilityType(dto.getDisabilityType());
        d.setDisabilityDescription(dto.getDisabilityDescription());

        disabilityRepository.save(d);
        
        // Broadcast step update event
        broadcastStepUpdate(application, "DISABILITY");
        
        return mapToDto(application);
    }

    @Override
    public ApplicationDto saveVulnerability(Long appId, Long registerId, VulnerabilityDto dto) {
        Application application = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(application);

        VulnerabilityInformation v = getOrCreate(pi::getVulnerabilityInformation, VulnerabilityInformation::new);
        v.setPersonalInformation(pi);
        v.setHouseholdIncome(dto.getHouseholdIncome());
        v.setHasChildcareNeeds(dto.getHasChildcareNeeds());
        v.setDescription(dto.getDescription());

        vulnerabilityRepository.save(v);
        
        // Broadcast step update event
        broadcastStepUpdate(application, "VULNERABILITY");
        
        return mapToDto(application);
    }

    @Override
    public ApplicationDto saveDocuments(Long appId, Long registerId, List<DocumentDto> dtos) {
        Application application = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(application);

        documentRepository.deleteByPersonalInformation(pi);
        documentRepository.flush();

        List<Document> documents = dtos.stream().map(dto -> {
            Document doc = new Document();
            doc.setPersonalInformation(pi);
            doc.setDocType(dto.getDocType());
            doc.setFileUrl(dto.getFileUrl());
            return doc;
        }).collect(Collectors.toList());

        documentRepository.saveAll(documents);
        
        // Broadcast step update event
        broadcastStepUpdate(application, "DOCUMENTS");
        
        return mapToDto(application);
    }

    @Override
    public ApplicationDto saveEmergencyContacts(Long appId, Long registerId, List<EmergencyContactDto> dtos) {
        Application application = getOwnedApplication(appId, registerId);
        PersonalInformation pi = ensurePersonalInfo(application);

        emergencyContactRepository.deleteByPersonalInformation(pi);
        emergencyContactRepository.flush();

        List<EmergencyContact> contacts = dtos.stream().map(dto -> {
            EmergencyContact contact = new EmergencyContact();
            contact.setPersonalInformation(pi);
            contact.setName(dto.getName());
            contact.setRelationship(dto.getRelationship());
            contact.setPhone(dto.getPhone());
            return contact;
        }).collect(Collectors.toList());

        emergencyContactRepository.saveAll(contacts);
        
        // Broadcast step update event
        broadcastStepUpdate(application, "EMERGENCY_CONTACTS");
        
        return mapToDto(application);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationDto getApplicationForUser(Long userId) {
        Application application = applicationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No application found for user with ID: " + userId));
        return mapToDto(application);
    }

    @Override
    public ApplicationDto submitApplication(Long appId, Long registerId) {
        Application app = getOwnedApplication(appId, registerId);

        // Validated submission logic
        applicationValidationService.validateForSubmission(app);

        app.setStatus(ApplicationStatus.SUBMITTED);
        app.setSubmittedAt(LocalDateTime.now());
        Application savedApp = applicationRepository.save(app);
        
        // Broadcast application submitted event
        webSocketService.broadcastApplicationUpdate(Map.of(
            "event", "APPLICATION_SUBMITTED",
            "applicationId", savedApp.getId(),
            "userId", savedApp.getUser().getId(),
            "status", ApplicationStatus.SUBMITTED,
            "submittedAt", savedApp.getSubmittedAt()
        ));
        
        // Notify admin dashboard
        webSocketService.broadcastToAdmin("applications", Map.of(
            "event", "NEW_SUBMISSION",
            "applicationId", savedApp.getId(),
            "applicantName", savedApp.getPersonalInformation() != null ? 
                savedApp.getPersonalInformation().getFullName() : "Unknown"
        ));
        
        return mapToDto(savedApp);
    }

    // FIX: Method signature now matches interface (includes userId)
    @Override
    public double calculateCompletionPercentage(Long applicationId, Long userId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found"));

        // SECURITY CHECK: Ensure user owns the application
        if (!app.getUser().getId().equals(userId)) {
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

    private Application getOwnedApplication(Long appId, Long registerId) {
        Application app = applicationRepository.findById(appId)
                .orElseThrow(() -> new NotFoundException("Application not found"));

        // Security Fix: Use AccessDeniedException
        if (!app.getUser().getRegister().getId().equals(registerId)) {
            throw new AccessDeniedException("Access denied: You do not own this application.");
        }
        
        // Prevent editing submitted applications
        if (app.getStatus() != ApplicationStatus.DRAFT) {
            throw new ValidationException("Cannot edit application with status: " + app.getStatus() + ". Only DRAFT applications can be edited.");
        }
        
        return app;
    }

    private PersonalInformation ensurePersonalInfo(Application app) {
        if (app.getPersonalInformation() == null) {
            PersonalInformation pi = new PersonalInformation();
            pi.setApplication(app);
            // FIX: Set bidirectional relationship to ensure persistence
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
        double progress = calculateCompletionPercentage(application.getId(), application.getUser().getId());
        
        // Broadcast to user
        webSocketService.broadcastApplicationProgress(
            application.getUser().getId().toString(),
            Map.of(
                "event", "STEP_UPDATED",
                "applicationId", application.getId(),
                "step", stepName,
                "progress", progress
            )
        );
        
        // Broadcast to admin dashboard
        webSocketService.broadcastToAdmin("progress", Map.of(
            "event", "APPLICATION_PROGRESS",
            "applicationId", application.getId(),
            "step", stepName,
            "progress", progress,
            "userId", application.getUser().getId()
        ));
    }

    private ApplicationDto mapToDto(Application app) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(app.getId());
        dto.setUserId(app.getUser().getId());
        dto.setStatus(app.getStatus());

        // Fix: Null safe check for cohort
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
            piDto.setMaritalStatus(pi.getMaritalStatus());
            piDto.setSocialLinks(pi.getSocialLinks());
            piDto.setAdditionalInformation(pi.getAdditionalInformation());
            dto.setPersonalInfo(piDto);

            if (pi.getEducationOccupation() != null) {
                EducationOccupation edu = pi.getEducationOccupation();
                EducationDto eduDto = new EducationDto();
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