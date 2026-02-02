package com.company.mortgage.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MortgageCheckRequest(
        @NotNull(message = "Income is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Income must be greater than 0")
        BigDecimal income,

        @Min(value = 1, message = "Maturity period must be at least 1 year")
        int maturityPeriod,

        @NotNull(message = "Loan value is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Loan value must be greater than 0")
        BigDecimal loanValue,


        @NotNull(message = "Home value is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Home value must be greater than 0")
        BigDecimal homeValue
) {
}
