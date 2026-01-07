package com.igirerwanda.application_portal_backend.application.repository;

import com.igirerwanda.application_portal_backend.application.entity.EmergencyContact;
import com.igirerwanda.application_portal_backend.application.entity.PersonalInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {
    List<EmergencyContact> findByPersonalInformation(PersonalInformation personalInformation);
    void deleteByPersonalInformation(PersonalInformation personalInformation);
}