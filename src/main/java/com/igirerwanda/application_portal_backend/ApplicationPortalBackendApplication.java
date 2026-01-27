package com.igirerwanda.application_portal_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EntityScan("com.igirerwanda.application_portal_backend")
@EnableJpaRepositories("com.igirerwanda.application_portal_backend")
public class ApplicationPortalBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApplicationPortalBackendApplication.class, args);
	}
}

