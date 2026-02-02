package com.igirerwanda.application_portal_backend.me.repository;

import com.igirerwanda.application_portal_backend.me.entity.MasterUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MasterUserRepository extends JpaRepository<MasterUser, UUID> {

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByOriginSystemId(String originSystemId);

    boolean existsByFullNameIgnoreCase(String fullName);
}