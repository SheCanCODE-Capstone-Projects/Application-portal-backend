package com.igirerwanda.application_portal_backend.application.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "disability_information")
@Getter
@Setter
public class DisabilityInformation {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private PersonalInformation personalInformation;

    private Boolean hasDisability;
    private String disabilityType;

    @Column(columnDefinition = "TEXT")
    private String disabilityDescription;
}

