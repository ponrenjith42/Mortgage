package com.company.mortgage.configuration;

import com.company.mortgage.repository.model.InterestRateEntity;
import com.company.mortgage.service.InterestRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class InterestRateInitializer {

    private final InterestRateService interestRateService;
    private final MortgageRateConfig mortgageRateConfig;

    @Bean
    ApplicationRunner loadInterestRatesFromExternalFile() {
        return args -> {
            List<InterestRateEntity> entities =
                    mortgageRateConfig.getRates().stream()
                            .map(rate -> InterestRateEntity.builder()
                                    .maturityPeriod(rate.maturity())
                                    .interestRate(rate.interest())
                                    .lastUpdate(rate.lastUpdate())
                                    .build())
                            .toList();
            interestRateService.addRates(entities);
        };
    }
}