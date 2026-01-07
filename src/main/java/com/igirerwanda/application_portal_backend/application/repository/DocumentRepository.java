package com.igirerwanda.application_portal_backend.application.repository;

import com.igirerwanda.application_portal_backend.application.entity.Document;
import com.igirerwanda.application_portal_backend.application.entity.PersonalInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByPersonalInformation(PersonalInformation personalInformation);
    void deleteByPersonalInformation(PersonalInformation personalInformation);
}