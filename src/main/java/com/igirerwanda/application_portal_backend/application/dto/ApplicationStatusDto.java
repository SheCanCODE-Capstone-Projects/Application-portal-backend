package com.igirerwanda.application_portal_backend.application.dto;

import com.igirerwanda.application_portal_backend.common.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ApplicationStatusDto {
    private ApplicationStatus status;
}