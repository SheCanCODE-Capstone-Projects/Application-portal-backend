package com.igirerwanda.application_portal_backend.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentDto {
    @NotBlank(message = "Document type is required")
    private String docType;
    
    @NotBlank(message = "File URL is required")
    @Pattern(regexp = "^https?://.*", message = "Invalid URL format")
    private String fileUrl;
}