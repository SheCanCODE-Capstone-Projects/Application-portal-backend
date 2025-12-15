package com.igirerwanda.application_portal_backend.application.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "motivation_answers")
@Getter
@Setter
public class MotivationAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private PersonalInformation personalInformation;

    @Column(columnDefinition = "TEXT")
    private String whyJoin;

    @Column(columnDefinition = "TEXT")
    private String futureGoals;

    private String preferredCourse;
}

