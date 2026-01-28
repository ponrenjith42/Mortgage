package com.company.mortgage.response;

import java.math.BigDecimal;

public record MortgageCheckResponse(
        boolean feasible,
        BigDecimal monthlyCost) {
}
