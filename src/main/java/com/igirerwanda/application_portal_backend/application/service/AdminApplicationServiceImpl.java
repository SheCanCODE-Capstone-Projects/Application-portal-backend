package com.igirerwanda.application_portal_backend.application.service;

import com.igirerwanda.application_portal_backend.application.dto.*;
import com.igirerwanda.application_portal_backend.application.entity.*;
import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import com.igirerwanda.application_portal_backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminApplicationServiceImpl implements AdminApplicationService {

    private final ApplicationRepository applicationRepository;
    private final NotificationService notificationService;


    @Override
    public List<ApplicationDto> getAllActiveApplications() {
        return applicationRepository.findByDeletedFalseAndArchivedFalse().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDto> getApplicationsByStatus(ApplicationStatus status) {
        return applicationRepository.findByStatusAndDeletedFalse(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDto> getSystemRejectedApplications() {
        return applicationRepository.findByIsSystemRejectedTrueAndDeletedFalse().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDto> getArchivedApplications() {
        return applicationRepository.findByArchivedTrueAndDeletedFalse().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDto> getDeletedApplications() {
        return applicationRepository.findByDeletedTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationDto getApplicationDetails(Long applicationId) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found with id: " + applicationId));
        return mapToDto(app);
    }


    @Override
    public ApplicationDto acceptApplication(Long applicationId) {
        Application app = findById(applicationId);
        app.setStatus(ApplicationStatus.ACCEPTED);
        Application saved = applicationRepository.save(app);

        notificationService.sendApplicationAcceptedNotification(saved);

        return mapToDto(saved);
    }

    @Override
    public ApplicationDto rejectApplication(Long applicationId) {
        Application app = findById(applicationId);
        app.setStatus(ApplicationStatus.REJECTED);
        Application saved = applicationRepository.save(app);

        notificationService.sendApplicationRejectedNotification(saved);

        return mapToDto(saved);
    }

    @Override
    public ApplicationDto scheduleInterview(Long applicationId, InterviewScheduleRequest request) {
        Application app = findById(applicationId);

        app.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
        app.setInterviewDate(request.getInterviewDate());
        Application saved = applicationRepository.save(app);

        String instructions = request.getInstructions() != null
                ? request.getInstructions() : "No instructions provided";
        String details = String.format("Date: %s. Instructions: %s",
                request.getInterviewDate().toString(),
                instructions);

        notificationService.sendInterviewScheduledNotification(saved, details);

        return mapToDto(saved);
    }


    @Override
    public void softDeleteApplication(Long applicationId) {
        Application app = findById(applicationId);
        app.setDeleted(true);
        applicationRepository.save(app);
    }

    @Override
    public void archiveApplication(Long applicationId) {
        Application app = findById(applicationId);
        app.setArchived(true);
        applicationRepository.save(app);
    }

    @Override
    public void restoreApplication(Long applicationId) {
        Application app = findById(applicationId);
        app.setDeleted(false);
        app.setArchived(false);
        applicationRepository.save(app);
    }



    private Application findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Application not found with id: " + id));
    }


    private ApplicationDto mapToDto(Application app) {
        ApplicationDto dto = new ApplicationDto();
        dto.setId(app.getId());
        dto.setUserId(app.getUser().getId());
        dto.setStatus(app.getStatus());
        dto.setSystemRejected(app.isSystemRejected());
        dto.setSystemRejectionReason(app.getSystemRejectionReason());
        dto.setSubmittedAt(app.getSubmittedAt());
        dto.setCreatedAt(app.getCreatedAt());

        if (app.getCohort() != null) {
            dto.setCohortId(app.getCohort().getId());
            dto.setCohortName(app.getCohort().getName());
        }

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

            if (pi.getDocuments() != null) {
                List<DocumentDto> docDtos = pi.getDocuments().stream().map(doc -> {
                    DocumentDto dDto = new DocumentDto();
                    dDto.setDocType(doc.getDocType());
                    dDto.setFileUrl(doc.getFileUrl());
                    return dDto;
                }).collect(Collectors.toList());
                dto.setDocuments(docDtos);
            }

            if (pi.getEmergencyContacts() != null) {
                List<EmergencyContactDto> contactDtos = pi.getEmergencyContacts().stream().map(c -> {
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