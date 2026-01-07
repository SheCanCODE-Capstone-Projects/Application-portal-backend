package com.igirerwanda.application_portal_backend.cohort.entity;

import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set; // ✅ Must use Set, not List

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

    @Column(name = "is_open", nullable = true)
    private Boolean isOpen;

    @Column(name = "application_limit", nullable = true)
    private Integer applicationLimit;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "cohort_requirements", joinColumns = @JoinColumn(name = "cohort_id"))
    @Column(name = "requirement")
    private Set<String> requirements = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "cohort_rules", joinColumns = @JoinColumn(name = "cohort_id"))
    @Column(name = "rule")
    private Set<String> rules = new HashSet<>();

    @ElementCollection(targetClass = UserRole.class)
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles = new HashSet<>();

    private LocalDate startDate;
    private LocalDate endDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}