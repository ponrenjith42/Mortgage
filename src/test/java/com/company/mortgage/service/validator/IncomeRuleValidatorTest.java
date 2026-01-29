package com.company.mortgage.service.validator;

import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.service.exception.MortgageNotFeasibleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IncomeRuleValidatorTest {

    private final IncomeRuleValidator validator = new IncomeRuleValidator();

    @Test
    void validate_shouldPass_whenLoanWithinIncomeLimit() {
        MortgageCheckRequest request = new MortgageCheckRequest(
                new BigDecimal("50000"),
                10,
                new BigDecimal("150000"),
                new BigDecimal("200000")
        );
        // Should not throw exception
        validator.validate(request);
    }

    @Test
    void validate_shouldThrowException_whenLoanExceedsIncomeLimit() {
        MortgageCheckRequest request = new MortgageCheckRequest(
                new BigDecimal("30000"),
                10,
                new BigDecimal("150000"),
                new BigDecimal("200000")
        );
        assertThatThrownBy(() -> validator.validate(request))
                .isInstanceOf(MortgageNotFeasibleException.class)
                .hasMessageContaining("income");
    }
}