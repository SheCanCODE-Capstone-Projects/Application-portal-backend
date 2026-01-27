package com.igirerwanda.application_portal_backend.application.entity;

import com.igirerwanda.application_portal_backend.common.enums.EducationalLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "education_occupation")
@Getter
@Setter
public class EducationOccupation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "personal_information_id")
    private PersonalInformation personalInformation;

    @Enumerated(EnumType.STRING)
    private EducationalLevel highestEducationLevel;
    
    private String highestEducation; // Keep for backward compatibility
    private String occupation;
    private String employmentStatus;
    private Integer yearsExperience;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

