package com.igirerwanda.application_portal_backend.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap; // <--- ADD THIS
import java.util.Map;     // <--- ADD THIS

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {
                "com.igirerwanda.application_portal_backend.admin",
                "com.igirerwanda.application_portal_backend.application",
                "com.igirerwanda.application_portal_backend.auth",
                "com.igirerwanda.application_portal_backend.cohort",
                "com.igirerwanda.application_portal_backend.notification",
                "com.igirerwanda.application_portal_backend.review",
                "com.igirerwanda.application_portal_backend.user"
        },
        entityManagerFactoryRef = "primaryEntityManagerFactory",
        transactionManagerRef = "primaryTransactionManager"
)
public class PrimaryDbConfig {

    @Primary
    @Bean(name = "primaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource primaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("primaryDataSource") DataSource dataSource) {


        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
        properties.put("hibernate.implicit_naming_strategy", "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");

        properties.put("hibernate.hbm2ddl.auto", "update");

        return builder
                .dataSource(dataSource)
                .packages(
                        "com.igirerwanda.application_portal_backend.admin.entity",
                        "com.igirerwanda.application_portal_backend.application.entity",
                        "com.igirerwanda.application_portal_backend.auth.entity",
                        "com.igirerwanda.application_portal_backend.cohort.entity",
                        "com.igirerwanda.application_portal_backend.notification.entity",
                        "com.igirerwanda.application_portal_backend.review.entity",
                        "com.igirerwanda.application_portal_backend.user.entity"
                )
                .properties(properties)
                .persistenceUnit("primary")
                .build();
    }

    @Primary
    @Bean(name = "primaryTransactionManager")
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("primaryEntityManagerFactory") EntityManagerFactory primaryEntityManagerFactory) {
        return new JpaTransactionManager(primaryEntityManagerFactory);
    }
}