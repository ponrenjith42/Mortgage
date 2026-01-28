package com.company.mortgage.configuration;

import com.company.mortgage.repository.model.MortgageRate;
import com.company.mortgage.service.MortgageRateService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class MortgageRateConfig {

    @Bean
    ApplicationRunner initializeMortgageRates(MortgageRateService mortgageRateService) {
        return args -> {
            List<MortgageRate> rates = List.of(
                    MortgageRate.builder().maturityPeriod(5).interestRate(new BigDecimal("2.0")).build(),
                    MortgageRate.builder().maturityPeriod(10).interestRate(new BigDecimal("3.0")).build(),
                    MortgageRate.builder().maturityPeriod(15).interestRate(new BigDecimal("3.5")).build(),
                    MortgageRate.builder().maturityPeriod(20).interestRate(new BigDecimal("4.0")).build()
            );
            mortgageRateService.addRates(rates);
        };
    }
}