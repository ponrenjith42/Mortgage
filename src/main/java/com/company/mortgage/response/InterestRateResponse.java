package com.company.mortgage.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InterestRateResponse(int maturityPeriod,
                                   BigDecimal interestRate,
                                   LocalDateTime lastUpdate ) {
}
