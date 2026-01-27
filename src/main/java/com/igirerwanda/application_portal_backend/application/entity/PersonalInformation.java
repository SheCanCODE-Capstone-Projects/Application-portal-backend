package com.igirerwanda.application_portal_backend.application.entity;

import com.igirerwanda.application_portal_backend.common.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "personal_information")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PersonalInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "application_id")
    private Application application;

    private String fullName;
    private String email;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String nationality;
    private String maritalStatus;
    private String socialLinks;

    @Column(columnDefinition = "TEXT")
    private String additionalInformation;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "personalInformation", cascade = CascadeType.ALL)
    private List<Document> documents;

    @OneToMany(mappedBy = "personalInformation", cascade = CascadeType.ALL)
    private List<EmergencyContact> emergencyContacts;

    @OneToOne(mappedBy = "personalInformation", cascade = CascadeType.ALL)
    private EducationOccupation educationOccupation;

    @OneToOne(mappedBy = "personalInformation", cascade = CascadeType.ALL)
    private MotivationAnswer motivationAnswer;

    @OneToOne(mappedBy = "personalInformation", cascade = CascadeType.ALL)
    private DisabilityInformation disabilityInformation;

    @OneToOne(mappedBy = "personalInformation", cascade = CascadeType.ALL)
    private VulnerabilityInformation vulnerabilityInformation;
}