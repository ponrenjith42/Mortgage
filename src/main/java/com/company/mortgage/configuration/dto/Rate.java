package com.company.mortgage.configuration.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Rate(
        int maturity,
        BigDecimal interest,
        LocalDateTime lastUpdate
) {}