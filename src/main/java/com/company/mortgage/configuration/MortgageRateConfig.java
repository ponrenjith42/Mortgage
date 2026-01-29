package com.company.mortgage.configuration;

import com.company.mortgage.repository.model.InterestRateEntity;
import com.company.mortgage.service.InterestRateService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class MortgageRateConfig {

    @Bean
    ApplicationRunner initializeMortgageRates(InterestRateService interestRateService) {
        return args -> {
            List<InterestRateEntity> rates = List.of(
                    InterestRateEntity.builder().maturityPeriod(5).interestRate(new BigDecimal("2.0")).build(),
                    InterestRateEntity.builder().maturityPeriod(10).interestRate(new BigDecimal("3.0")).build(),
                    InterestRateEntity.builder().maturityPeriod(15).interestRate(new BigDecimal("3.5")).build(),
                    InterestRateEntity.builder().maturityPeriod(20).interestRate(new BigDecimal("4.0")).build()
            );
            interestRateService.addRates(rates);
        };
    }
}