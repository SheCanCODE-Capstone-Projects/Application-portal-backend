package com.igirerwanda.application_portal_backend.service;

import com.igirerwanda.application_portal_backend.cohort.dto.CohortCreateDto;
import com.igirerwanda.application_portal_backend.cohort.dto.CohortDto;
import com.igirerwanda.application_portal_backend.cohort.entity.Cohort;
import com.igirerwanda.application_portal_backend.cohort.mapper.CohortMapper;
import com.igirerwanda.application_portal_backend.cohort.repository.CohortRepository;
import com.igirerwanda.application_portal_backend.cohort.service.CohortServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CohortServiceTest {

    @Mock private CohortRepository cohortRepository;
    @Mock private CohortMapper cohortMapper;

    @InjectMocks
    private CohortServiceImpl cohortService;

    @Test
    void createCohort_Success() {
        CohortCreateDto dto = new CohortCreateDto();
        dto.setName("Cohort 1");
        dto.setStartDate(LocalDate.now());

        Cohort cohort = new Cohort();
        cohort.setId(1L);

        when(cohortMapper.toEntity(dto)).thenReturn(cohort);
        when(cohortRepository.save(any(Cohort.class))).thenReturn(cohort);
        when(cohortMapper.toDto(cohort)).thenReturn(new CohortDto());

        CohortDto result = cohortService.createCohort(dto);

        assertNotNull(result);
        verify(cohortRepository).save(cohort);
    }
}