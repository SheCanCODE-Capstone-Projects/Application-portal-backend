package com.igirerwanda.application_portal_backend.application.repository;

import com.igirerwanda.application_portal_backend.application.entity.PersonalInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalInfoRepository extends JpaRepository<PersonalInformation, Long> {
}