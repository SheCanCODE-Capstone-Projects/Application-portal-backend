package com.igirerwanda.application_portal_backend.cohort.entity;

import com.igirerwanda.application_portal_backend.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "cohorts", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cohort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @ElementCollection
    private List<String> rules;

    @ElementCollection
    private List<String> requirements;

    @ElementCollection(targetClass = UserRole.class)
    @Enumerated(EnumType.STRING)
    private List<UserRole> roles;
}
