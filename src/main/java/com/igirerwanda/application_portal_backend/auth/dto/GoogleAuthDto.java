package com.igirerwanda.application_portal_backend.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleAuthDto {
    private String email;
    private String googleId;
    private String name;
}
