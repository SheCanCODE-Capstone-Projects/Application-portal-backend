package com.igirerwanda.application_portal_backend.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InterviewScheduleRequest {
    @NotNull(message = "Interview date and time is required")
    @Future(message = "Interview time must be in the future")
    private LocalDateTime interviewDate;

    private String instructions;

    // Add this missing field so the service can access getMeetingLink()
    private String meetingLink;
}