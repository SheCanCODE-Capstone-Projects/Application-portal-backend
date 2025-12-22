package com.igirerwanda.application_portal_backend.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth/google")
public class GoogleAuthController {

    @GetMapping("/login")
    public void login(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/signup")
    public void signup(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google?prompt=select_account");
    }
}
