package com.igirerwanda.application_portal_backend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DbLogger {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @PostConstruct
    public void log() {
        System.out.println("Connecting to DB: " + dbUrl);
    }
}
