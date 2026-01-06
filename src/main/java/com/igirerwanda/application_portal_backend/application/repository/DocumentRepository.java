package com.igirerwanda.application_portal_backend.application.repository;

import com.igirerwanda.application_portal_backend.application.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByPersonalInformationId(Long personalInformationId);
    List<Document> findByDocType(String docType);
    void deleteByPersonalInformationId(Long personalInformationId);
}