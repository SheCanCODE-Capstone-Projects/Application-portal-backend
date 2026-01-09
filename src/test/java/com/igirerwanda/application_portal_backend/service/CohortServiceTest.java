package com.igirerwanda.application_portal_backend.service;

import com.igirerwanda.application_portal_backend.cohort.dto.CohortCreateDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortDto;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import com.igirerwanda.application_portal_backend.cohort.service.CohortServiceImpl;
import com.igirerwanda.application_portal_backend.common.exception.DuplicateResourceException;
import com.igirerwanda.application_portal_backend.common.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CohortServiceImplTest {

    @Mock private CohortRepository repository;
    @InjectMocks private CohortServiceImpl cohortService;

    @Test
    void createCohort_Success() {
        // Given
        CohortCreateDto dto = new CohortCreateDto();
        dto.setName("Cohort 1");
        dto.setStartDate(LocalDate.now());
        dto.setEndDate(LocalDate.now().plusMonths(3));

        when(repository.findByName("Cohort 1")).thenReturn(Optional.empty());

        Cohort savedCohort = new Cohort();
        savedCohort.setId(1L);
        savedCohort.setName("Cohort 1");
        when(repository.save(any(Cohort.class))).thenReturn(savedCohort);

        // When
        CohortDto result = cohortService.createCohort(dto);

        // Then
        assertNotNull(result);
        assertEquals("Cohort 1", result.getName());
    }

    @Test
    void createCohort_DuplicateName_ThrowsException() {
        // Given
        CohortCreateDto dto = new CohortCreateDto();
        dto.setName("Cohort 1");

        when(repository.findByName("Cohort 1")).thenReturn(Optional.of(new Cohort()));

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> cohortService.createCohort(dto));
    }

    @Test
    void deleteCohort_NotFound_ThrowsException() {
        // Given
        when(repository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThrows(NotFoundException.class, () -> cohortService.deleteCohort(1L));
    }
}