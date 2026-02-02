package com.company.mortgage.configuration;

import com.company.mortgage.configuration.dto.Rate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "mortgage")
@Getter
@Setter
public class MortgageRateConfig {
    private List<Rate> rates;
}