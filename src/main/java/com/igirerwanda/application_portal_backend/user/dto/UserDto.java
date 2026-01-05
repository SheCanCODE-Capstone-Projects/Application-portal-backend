package com.igirerwanda.application_portal_backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserDto {
    private Long id;
    private Long registerId;
    private String status;
    private String email;
    private String username;
}
