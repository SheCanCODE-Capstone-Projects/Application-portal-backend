package com.igirerwanda.application_portal_backend.application.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "motivation_answers")
@Getter
@Setter
public class MotivationAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "personal_information_id")
    private PersonalInformation personalInformation;

    @Column(columnDefinition = "TEXT")
    private String whyJoin;

    @Column(columnDefinition = "TEXT")
    private String futureGoals;

    private String preferredCourse;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

