package com.igirerwanda.application_portal_backend.application.repository;

import com.igirerwanda.application_portal_backend.application.entity.MotivationAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MotivationAnswerRepository extends JpaRepository<MotivationAnswer, Long> {
    Optional<MotivationAnswer> findByPersonalInformationId(Long personalInformationId);
    void deleteByPersonalInformationId(Long personalInformationId);
}