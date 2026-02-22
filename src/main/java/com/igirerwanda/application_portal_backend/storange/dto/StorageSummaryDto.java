package com.igirerwanda.application_portal_backend.storange.dto;

import com.igirerwanda.application_portal_backend.application.dto.ApplicationDto;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class StorageSummaryDto {
    // Structure: Map<Year, Map<CohortName, List<ApplicationDto>>>
    private Map<Integer, Map<String, List<ApplicationDto>>> storageTree;
}