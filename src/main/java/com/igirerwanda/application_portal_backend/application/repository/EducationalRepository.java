package com.igirerwanda.application_portal_backend.application.repository;

import com.igirerwanda.application_portal_backend.application.entity.EducationOccupation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EducationalRepository extends JpaRepository<EducationOccupation, Long> {
    Optional<EducationOccupation> findByPersonalInformationId(Long personalInformationId);
    void deleteByPersonalInformationId(Long personalInformationId);
}
