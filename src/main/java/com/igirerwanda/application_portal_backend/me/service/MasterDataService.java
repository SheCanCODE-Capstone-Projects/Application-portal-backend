package com.igirerwanda.application_portal_backend.me.service;

import com.igirerwanda.application_portal_backend.application.entity.Application;
import com.igirerwanda.application_portal_backend.application.entity.PersonalInformation;
import com.igirerwanda.application_portal_backend.me.entity.MasterUser;
import com.igirerwanda.application_portal_backend.me.repository.MasterUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Fixes "Cannot resolve symbol 'log'"
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MasterDataService {

    private final MasterUserRepository masterUserRepository;

    // Gate 2: Check existence (Used during submission)
    @Transactional(readOnly = true, transactionManager = "masterTransactionManager")
    public boolean isUserInMasterDatabase(PersonalInformation pi) {
        if (pi == null) return false;

        // Check by Phone Number
        if (masterUserRepository.existsByPhoneNumber(pi.getPhone())) {
            log.warn("System Rejection: Phone {} already exists in Master DB", pi.getPhone());
            return true;
        }

        // Check by Name (Optional, as requested)
        if (masterUserRepository.existsByFullNameIgnoreCase(pi.getFullName())) {
            log.warn("System Rejection: Name {} already exists in Master DB", pi.getFullName());
            return true;
        }

        return false;
    }

    // Sync Action (Used when Admin Accepts)
    @Transactional(transactionManager = "masterTransactionManager")
    public void syncUserToMaster(Application app) {
        PersonalInformation pi = app.getPersonalInformation();

        if (pi == null) return;

        // Prevent duplicate sync
        if (masterUserRepository.existsByPhoneNumber(pi.getPhone())) {
            log.info("User {} already in Master DB, skipping sync.", pi.getFullName());
            return;
        }

        MasterUser master = new MasterUser();

        // 1. Full Name
        master.setFullName(pi.getFullName());

        // 2. Phone Number
        master.setPhoneNumber(pi.getPhone());

        // 3. Unique ID from Origin (Using User UUID or National ID if available)
        master.setOriginSystemId(app.getUser().getId().toString());

        // 4. Origin System Name
        master.setOriginSystem("IGIRERWANDA_PORTAL");

        // 5. Cohort Joined
        master.setCohortJoined(app.getCohort().getName());

        // 6. Date Applied
        master.setApplicationDate(app.getSubmittedAt());

        masterUserRepository.save(master);
        log.info("Successfully synced {} to Master Data.", pi.getFullName());
    }
}