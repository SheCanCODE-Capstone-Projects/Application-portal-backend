package com.igirerwanda.application_portal_backend.application.repository;

import com.igirerwanda.application_portal_backend.application.entity.DisabilityInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DisabilityRepository extends JpaRepository<DisabilityInformation, Long> {
    Optional<DisabilityInformation> findByPersonalInformationId(Long personalInformationId);
    void deleteByPersonalInformationId(Long personalInformationId);
}
