package com.igirerwanda.application_portal_backend.application.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "personal_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "application_id")
    private Application application;

    private String fullName;
    private String email;
    private String phone;

    private String maritalStatus;
    private String socialLinks;

    @Column(columnDefinition = "TEXT")
    private String additionalInformation;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationships as per ERD
    @OneToMany(mappedBy = "personalInformation", cascade = CascadeType.ALL)
    private java.util.List<Document> documents;

    @OneToMany(mappedBy = "personalInformation", cascade = CascadeType.ALL)
    private java.util.List<EmergencyContact> emergencyContacts;

    @OneToOne(mappedBy = "personalInformation", cascade = CascadeType.ALL)
    private EducationOccupation educationOccupation;

    @OneToOne(mappedBy = "personalInformation", cascade = CascadeType.ALL)
    private MotivationAnswer motivationAnswer;

    @OneToOne(mappedBy = "personalInformation", cascade = CascadeType.ALL)
    private DisabilityInformation disabilityInformation;

    @OneToOne(mappedBy = "personalInformation", cascade = CascadeType.ALL)
    private VulnerabilityInformation vulnerabilityInformation;
}
