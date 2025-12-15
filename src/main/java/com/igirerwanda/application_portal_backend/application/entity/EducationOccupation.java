package com.igirerwanda.application_portal_backend.application.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "education_occupation")
@Getter
@Setter
public class EducationOccupation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private PersonalInformation personalInformation;

    private String highestEducation;
    private String occupation;
    private String employmentStatus;
    private Integer yearsExperience;
}

