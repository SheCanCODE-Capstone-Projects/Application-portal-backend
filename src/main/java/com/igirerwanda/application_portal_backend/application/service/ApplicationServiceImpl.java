package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.*;
import com.igirerwanda.application_portal_backend.application.entity.*;
import com.igirerwanda.application_portal_backend.application.repository.*;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.common.exception.DuplicateResourceException;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import com.igirerwanda.application_portal_backend.notification.service.WebSocketService;
import com.igirerwanda.application_portal_backend.user.entity.User;
import com.igirerwanda.application_portal_backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final PersonalInfoRepository personalInfoRepository;
    private final EducationalRepository educationalRepository;
    private final DocumentRepository documentRepository;
    private final EmergencyContactRepository emergencyContactRepository;
    private final MotivationAnswerRepository motivationAnswerRepository;
    private final DisabilityRepository disabilityRepository;
    private final VulnerabilityInformationRepository vulnerabilityRepository;
    private final CohortRepository cohortRepository;
    private final UserService userService;
    private final ApplicationValidationService validationService;
    private final WebSocketService webSocketService;

    @Override
    public ApplicationDto createApplication(Long userId, ApplicationCreateDto dto) {
        // Check if application already exists
        if (applicationRepository.existsByUserIdAndCohortId(userId, dto.getCohortId())) {
            throw new DuplicateResourceException("Application already exists for this cohort");
        }

        User user = userService.findById(userId);
        Cohort cohort = cohortRepository.findById(dto.getCohortId())
                .orElseThrow(() -> new NotFoundException("Cohort not found"));

        Application application = new Application();
        application.setUser(user);
        application.setCohort(cohort);
        application.setStatus(ApplicationStatus.DRAFT);

        application = applicationRepository.save(application);
        return mapToDto(application);
    }

    @Override
    public ApplicationDto submitCompleteApplication(Long userId, ApplicationSubmissionDto dto) {
        ApplicationCreateDto createDto = new ApplicationCreateDto();
        createDto.setCohortId(dto.getCohortId());
        ApplicationDto application = createApplication(userId, createDto);
        
        Long appId = application.getId();
        
        // Save all sections
        updatePersonalInfo(appId, dto.getPersonalInfo());
        updateEducation(appId, dto.getEducation());
        updateMotivation(appId, dto.getMotivation());
        
        if (dto.getDocuments() != null) {
            dto.getDocuments().forEach(doc -> addDocument(appId, doc));
        }
        
        if (dto.getEmergencyContacts() != null) {
            dto.getEmergencyContacts().forEach(contact -> addEmergencyContact(appId, contact));
        }
        
        if (dto.getDisability() != null) {
            updateDisability(appId, dto.getDisability());
        }
        
        if (dto.getVulnerability() != null) {
            updateVulnerability(appId, dto.getVulnerability());
        }
        
        return submitApplication(appId);
    }

    @Override
    public ApplicationDto updatePersonalInfo(Long applicationId, PersonalInfoDto dto) {
        Application application = findApplicationById(applicationId);
        
        PersonalInformation personalInfo = application.getPersonalInformation();
        if (personalInfo == null) {
            personalInfo = new PersonalInformation();
            personalInfo.setApplication(application);
        }
        
        personalInfo.setFullName(dto.getFullName());
        personalInfo.setEmail(dto.getEmail());
        personalInfo.setPhone(dto.getPhone());
        personalInfo.setMaritalStatus(dto.getMaritalStatus());
        personalInfo.setSocialLinks(dto.getSocialLinks());
        personalInfo.setAdditionalInformation(dto.getAdditionalInformation());
        
        personalInfoRepository.save(personalInfo);
        return mapToDto(application);
    }

    @Override
    public ApplicationDto updateEducation(Long applicationId, EducationDto dto) {
        Application application = findApplicationById(applicationId);
        PersonalInformation personalInfo = getOrCreatePersonalInfo(application);
        
        EducationOccupation education = personalInfo.getEducationOccupation();
        if (education == null) {
            education = new EducationOccupation();
            education.setPersonalInformation(personalInfo);
        }
        
        education.setHighestEducation(dto.getHighestEducation());
        education.setOccupation(dto.getOccupation());
        education.setEmploymentStatus(dto.getEmploymentStatus());
        education.setYearsExperience(dto.getYearsExperience());
        
        educationalRepository.save(education);
        return mapToDto(application);
    }

    @Override
    public ApplicationDto updateMotivation(Long applicationId, MotivationDto dto) {
        Application application = findApplicationById(applicationId);
        PersonalInformation personalInfo = getOrCreatePersonalInfo(application);
        
        MotivationAnswer motivation = personalInfo.getMotivationAnswer();
        if (motivation == null) {
            motivation = new MotivationAnswer();
            motivation.setPersonalInformation(personalInfo);
        }
        
        motivation.setWhyJoin(dto.getWhyJoin());
        motivation.setFutureGoals(dto.getFutureGoals());
        motivation.setPreferredCourse(dto.getPreferredCourse());
        
        motivationAnswerRepository.save(motivation);
        return mapToDto(application);
    }

    @Override
    public ApplicationDto addDocument(Long applicationId, DocumentDto dto) {
        Application application = findApplicationById(applicationId);
        PersonalInformation personalInfo = getOrCreatePersonalInfo(application);
        
        Document document = new Document();
        document.setPersonalInformation(personalInfo);
        document.setDocType(dto.getDocType());
        document.setFileUrl(dto.getFileUrl());
        
        documentRepository.save(document);
        return mapToDto(application);
    }

    @Override
    public ApplicationDto addEmergencyContact(Long applicationId, EmergencyContactDto dto) {
        Application application = findApplicationById(applicationId);
        PersonalInformation personalInfo = getOrCreatePersonalInfo(application);
        
        EmergencyContact contact = new EmergencyContact();
        contact.setPersonalInformation(personalInfo);
        contact.setName(dto.getName());
        contact.setRelationship(dto.getRelationship());
        contact.setPhone(dto.getPhone());
        
        emergencyContactRepository.save(contact);
        return mapToDto(application);
    }

    @Override
    public ApplicationDto updateDisability(Long applicationId, DisabilityDto dto) {
        Application application = findApplicationById(applicationId);
        PersonalInformation personalInfo = getOrCreatePersonalInfo(application);
        
        DisabilityInformation disability = personalInfo.getDisabilityInformation();
        if (disability == null) {
            disability = new DisabilityInformation();
            disability.setPersonalInformation(personalInfo);
        }
        
        disability.setHasDisability(dto.getHasDisability());
        disability.setDisabilityType(dto.getDisabilityType());
        disability.setDisabilityDescription(dto.getDisabilityDescription());
        
        disabilityRepository.save(disability);
        return mapToDto(application);
    }

    @Override
    public ApplicationDto updateVulnerability(Long applicationId, VulnerabilityDto dto) {
        Application application = findApplicationById(applicationId);
        PersonalInformation personalInfo = getOrCreatePersonalInfo(application);
        
        VulnerabilityInformation vulnerability = personalInfo.getVulnerabilityInformation();
        if (vulnerability == null) {
            vulnerability = new VulnerabilityInformation();
            vulnerability.setPersonalInformation(personalInfo);
        }
        
        vulnerability.setHouseholdIncome(dto.getHouseholdIncome());
        vulnerability.setHasChildcareNeeds(dto.getHasChildcareNeeds());
        vulnerability.setDescription(dto.getDescription());
        
        vulnerabilityRepository.save(vulnerability);
        return mapToDto(application);
    }

    @Override
    public ApplicationDto submitApplication(Long applicationId) {
        Application application = findApplicationById(applicationId);
        
        // Validate application completeness
        validationService.validateForSubmission(application);
        
        application.setStatus(ApplicationStatus.SUBMITTED);
        application.setSubmittedAt(LocalDateTime.now());
        
        application = applicationRepository.save(application);
        
        // Broadcast application submission
        ApplicationDto applicationDto = mapToDto(application);
        webSocketService.broadcastApplicationUpdate(applicationDto);
        webSocketService.broadcastToAdmin("applications", applicationDto);
        
        return applicationDto;
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationDto getApplication(Long applicationId) {
        Application application = findApplicationById(applicationId);
        return mapToDto(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationDto> getUserApplications(Long userId) {
        List<Application> applications = applicationRepository.findByUserId(userId);
        return applications.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationDto> getApplicationsByStatus(ApplicationStatus status) {
        List<Application> applications = applicationRepository.findByStatus(status);
        return applications.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public void deleteDocument(Long documentId) {
        documentRepository.deleteById(documentId);
    }

    @Override
    public void deleteEmergencyContact(Long contactId) {
        emergencyContactRepository.deleteById(contactId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isApplicationComplete(Long applicationId) {
        Application application = findApplicationById(applicationId);
        return validationService.isApplicationComplete(application);
    }

    // Helper methods
    private Application findApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Application not found"));
    }

    private PersonalInformation getOrCreatePersonalInfo(Application application) {
        PersonalInformation personalInfo = application.getPersonalInformation();
        if (personalInfo == null) {
            personalInfo = new PersonalInformation();
            personalInfo.setApplication(application);
            personalInfo = personalInfoRepository.save(personalInfo);
        }
        return personalInfo;
    }

    private ApplicationDto mapToDto(Application application) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(application.getId());
        dto.setUserId(application.getUser().getId());
        dto.setCohortId(application.getCohort().getId());
        dto.setCohortName(application.getCohort().getName());
        dto.setStatus(application.getStatus());
        dto.setSystemRejected(application.isSystemRejected());
        dto.setSubmittedAt(application.getSubmittedAt());
        dto.setCreatedAt(application.getCreatedAt());
        
        PersonalInformation personalInfo = application.getPersonalInformation();
        if (personalInfo != null) {
            dto.setPersonalInfo(mapPersonalInfoToDto(personalInfo));
            
            if (personalInfo.getEducationOccupation() != null) {
                dto.setEducation(mapEducationToDto(personalInfo.getEducationOccupation()));
            }
            
            if (personalInfo.getMotivationAnswer() != null) {
                dto.setMotivation(mapMotivationToDto(personalInfo.getMotivationAnswer()));
            }
            
            if (personalInfo.getDisabilityInformation() != null) {
                dto.setDisability(mapDisabilityToDto(personalInfo.getDisabilityInformation()));
            }
            
            if (personalInfo.getVulnerabilityInformation() != null) {
                dto.setVulnerability(mapVulnerabilityToDto(personalInfo.getVulnerabilityInformation()));
            }
            
            if (personalInfo.getDocuments() != null) {
                dto.setDocuments(personalInfo.getDocuments().stream()
                        .map(this::mapDocumentToDto).collect(Collectors.toList()));
            }
            
            if (personalInfo.getEmergencyContacts() != null) {
                dto.setEmergencyContacts(personalInfo.getEmergencyContacts().stream()
                        .map(this::mapEmergencyContactToDto).collect(Collectors.toList()));
            }
        }
        
        return dto;
    }

    private PersonalInfoDto mapPersonalInfoToDto(PersonalInformation entity) {
        PersonalInfoDto dto = new PersonalInfoDto();
        dto.setFullName(entity.getFullName());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        dto.setMaritalStatus(entity.getMaritalStatus());
        dto.setSocialLinks(entity.getSocialLinks());
        dto.setAdditionalInformation(entity.getAdditionalInformation());
        return dto;
    }

    private EducationDto mapEducationToDto(EducationOccupation entity) {
        EducationDto dto = new EducationDto();
        dto.setHighestEducation(entity.getHighestEducation());
        dto.setOccupation(entity.getOccupation());
        dto.setEmploymentStatus(entity.getEmploymentStatus());
        dto.setYearsExperience(entity.getYearsExperience());
        return dto;
    }

    private MotivationDto mapMotivationToDto(MotivationAnswer entity) {
        MotivationDto dto = new MotivationDto();
        dto.setWhyJoin(entity.getWhyJoin());
        dto.setFutureGoals(entity.getFutureGoals());
        dto.setPreferredCourse(entity.getPreferredCourse());
        return dto;
    }

    private DisabilityDto mapDisabilityToDto(DisabilityInformation entity) {
        DisabilityDto dto = new DisabilityDto();
        dto.setHasDisability(entity.getHasDisability());
        dto.setDisabilityType(entity.getDisabilityType());
        dto.setDisabilityDescription(entity.getDisabilityDescription());
        return dto;
    }

    private VulnerabilityDto mapVulnerabilityToDto(VulnerabilityInformation entity) {
        VulnerabilityDto dto = new VulnerabilityDto();
        dto.setHouseholdIncome(entity.getHouseholdIncome());
        dto.setHasChildcareNeeds(entity.getHasChildcareNeeds());
        dto.setDescription(entity.getDescription());
        return dto;
    }

    private DocumentDto mapDocumentToDto(Document entity) {
        DocumentDto dto = new DocumentDto();
        dto.setDocType(entity.getDocType());
        dto.setFileUrl(entity.getFileUrl());
        return dto;
    }

    private EmergencyContactDto mapEmergencyContactToDto(EmergencyContact entity) {
        EmergencyContactDto dto = new EmergencyContactDto();
        dto.setName(entity.getName());
        dto.setRelationship(entity.getRelationship());
        dto.setPhone(entity.getPhone());
        return dto;
    }
}
