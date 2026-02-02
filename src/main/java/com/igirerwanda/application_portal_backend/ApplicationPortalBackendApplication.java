package com.igirerwanda.application_portal_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

// FIX: Remove @EnableJpaRepositories and @EntityScan from here.
// They are now handled inside 'config/PrimaryDbConfig.java' and 'config/MasterDbConfig.java'
@SpringBootApplication
@EnableAsync
public class ApplicationPortalBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApplicationPortalBackendApplication.class, args);
	}
}