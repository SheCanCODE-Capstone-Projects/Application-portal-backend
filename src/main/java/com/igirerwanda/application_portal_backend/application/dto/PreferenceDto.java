package com.igirerwanda.application_portal_backend.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreferenceDto {
    private String preferredCourse;
    private String preferredSchedule;
    private String learningMode; // Online, Offline, Hybrid
    private String specialRequirements;
}
