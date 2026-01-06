package com.igirerwanda.application_portal_backend.cohort.entity;

import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cohorts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Cohort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String domain;
    private Integer year;
    private Boolean isOpen = true;
    private Integer applicationLimit;

    private String description;

    @ElementCollection
    private List<String> requirements;

    @ElementCollection
    private List<String> rules;

    @ElementCollection(targetClass = UserRole.class)
    @Enumerated(EnumType.STRING)
    private List<UserRole> roles;

    private LocalDate startDate;
    private LocalDate endDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    }

