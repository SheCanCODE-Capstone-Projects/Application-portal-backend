package com.igirerwanda.application_portal_backend.service;

import com.igirerwanda.application_portal_backend.application.dto.*;
import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.repository.ApplicationRepository;
import com.igirerwanda.application_portal_backend.application.service.UserApplicationService;
import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import com.igirerwanda.application_portal_backend.common.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MultiStepApplicationTest {

    @Autowired
    private UserApplicationService userApplicationService;
    
    @Autowired
    private ApplicationRepository applicationRepository;

    @Test
    public void testDraftFirstDesign() {
        // Test that applications start in DRAFT status
        Long userId = 1L; // Assuming test user exists
        
        ApplicationDto app = userApplicationService.startApplicationForUser(userId);
        
        assertEquals(ApplicationStatus.DRAFT, app.getStatus());
        assertNotNull(app.getId());
    }

    @Test
    public void testSingleActiveApplication() {
        Long userId = 1L;
        
        // Start first application
        ApplicationDto app1 = userApplicationService.startApplicationForUser(userId);
        
        // Start second application - should return the same one
        ApplicationDto app2 = userApplicationService.startApplicationForUser(userId);
        
        assertEquals(app1.getId(), app2.getId());
    }

    @Test
    public void testStepEditingOnlyForDraft() {
        Long userId = 1L;
        
        // Start application
        ApplicationDto app = userApplicationService.startApplicationForUser(userId);
        
        // Save personal info (should work for DRAFT)
        PersonalInfoDto personalInfo = new PersonalInfoDto();
        personalInfo.setFullName("Test User");
        personalInfo.setEmail("test@example.com");
        personalInfo.setPhone("+250123456789");
        
        assertDoesNotThrow(() -> 
            userApplicationService.savePersonalInfo(app.getId(), userId, personalInfo)
        );
        
        // Submit application
        userApplicationService.submitApplication(app.getId(), userId);
        
        // Try to edit after submission (should fail)
        assertThrows(ValidationException.class, () -> 
            userApplicationService.savePersonalInfo(app.getId(), userId, personalInfo)
        );
    }

    @Test
    public void testProgressCalculation() {
        Long userId = 1L;
        
        ApplicationDto app = userApplicationService.startApplicationForUser(userId);
        
        // Initially 0% (no steps completed)
        double initialProgress = userApplicationService.calculateCompletionPercentage(app.getId(), userId);
        assertEquals(0.0, initialProgress, 0.1);
        
        // Add personal info (1/7 steps = ~14.3%)
        PersonalInfoDto personalInfo = new PersonalInfoDto();
        personalInfo.setFullName("Test User");
        personalInfo.setEmail("test@example.com");
        personalInfo.setPhone("+250123456789");
        
        userApplicationService.savePersonalInfo(app.getId(), userId, personalInfo);
        
        double progressAfterPersonal = userApplicationService.calculateCompletionPercentage(app.getId(), userId);
        assertTrue(progressAfterPersonal > 0);
    }

    @Test
    public void testOwnershipValidation() {
        Long userId1 = 1L;
        Long userId2 = 2L; // Different user
        
        ApplicationDto app = userApplicationService.startApplicationForUser(userId1);
        
        PersonalInfoDto personalInfo = new PersonalInfoDto();
        personalInfo.setFullName("Test User");
        
        // User 2 trying to edit User 1's application should fail
        assertThrows(AccessDeniedException.class, () -> 
            userApplicationService.savePersonalInfo(app.getId(), userId2, personalInfo)
        );
    }

    @Test
    public void testListBasedStepReplacement() {
        Long userId = 1L;
        
        ApplicationDto app = userApplicationService.startApplicationForUser(userId);
        
        // Add personal info first
        PersonalInfoDto personalInfo = new PersonalInfoDto();
        personalInfo.setFullName("Test User");
        personalInfo.setEmail("test@example.com");
        personalInfo.setPhone("+250123456789");
        userApplicationService.savePersonalInfo(app.getId(), userId, personalInfo);
        
        // Add initial documents
        DocumentDto doc1 = new DocumentDto();
        doc1.setDocType("ID");
        doc1.setFileUrl("file1.pdf");
        
        userApplicationService.saveDocuments(app.getId(), userId, List.of(doc1));
        
        // Replace with new documents
        DocumentDto doc2 = new DocumentDto();
        doc2.setDocType("Diploma");
        doc2.setFileUrl("file2.pdf");
        
        ApplicationDto updatedApp = userApplicationService.saveDocuments(app.getId(), userId, List.of(doc2));
        
        // Should only have the new document
        assertEquals(1, updatedApp.getDocuments().size());
        assertEquals("Diploma", updatedApp.getDocuments().get(0).getDocType());
    }
}