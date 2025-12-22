package com.igirerwanda.application_portal_backend.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisabilityDto {
    private Boolean hasDisability;
    private String disabilityType;
    private String disabilityDescription;
}
