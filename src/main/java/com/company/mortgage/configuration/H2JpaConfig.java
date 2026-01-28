package com.company.mortgage.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Profile("h2")
@EnableJpaRepositories(basePackages = "com.company.mortgage.repository")
@EntityScan(basePackages = "com.company.mortgage.repository.model")
public class H2JpaConfig {
}
