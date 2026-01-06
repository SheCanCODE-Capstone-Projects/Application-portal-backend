package com.igirerwanda.application_portal_backend.application.repository;

import com.igirerwanda.application_portal_backend.application.entity.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {
    List<EmergencyContact> findByPersonalInformationId(Long personalInformationId);
    void deleteByPersonalInformationId(Long personalInformationId);
}