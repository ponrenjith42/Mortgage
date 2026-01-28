package com.company.mortgage.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MortgageCheckRequest(
        @NotNull
        BigDecimal income,

        @NotNull
        Integer maturityPeriod,

        @NotNull
        BigDecimal loanValue,

        @NotNull
        BigDecimal homeValue
) {
}
