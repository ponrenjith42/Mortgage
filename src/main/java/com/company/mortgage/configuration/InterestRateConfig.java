package com.company.mortgage.configuration;

import com.company.mortgage.service.MortgageRateService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class InterestRateConfig {

    @Bean
    ApplicationRunner initializeRates(MortgageRateService mortgageRateService) {
        return args -> {
            mortgageRateService.addRate(5, new BigDecimal("2.0"));
            mortgageRateService.addRate(10, new BigDecimal("3.0"));
            mortgageRateService.addRate(20, new BigDecimal("3.5"));
            mortgageRateService.addRate(30, new BigDecimal("4.0"));
        };
    }
}