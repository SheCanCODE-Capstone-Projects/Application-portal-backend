package com.igirerwanda.application_portal_backend.application.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Entity
@Table(name = "documents")
@Getter @Setter
public class Document {

    @jakarta.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private PersonalInformation personalInformation;

    private String docType;

    @Column(columnDefinition = "TEXT")
    private String fileUrl;
}


