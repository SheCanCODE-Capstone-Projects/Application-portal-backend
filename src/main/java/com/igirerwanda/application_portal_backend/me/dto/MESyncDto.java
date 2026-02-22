package com.igirerwanda.application_portal_backend.me.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MESyncDto {
    private UUID id;
    private String fullName;
    private String phoneNumber;
    private String originSystemId;
    private String cohortJoined;
    private LocalDateTime applicationDate;
    private String role;
    private String provider;
    private LocalDateTime syncedAt;
}