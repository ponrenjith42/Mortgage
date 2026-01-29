package com.company.mortgage.service.validator;

import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.service.exception.MortgageNotFeasibleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HomeValueRuleValidatorTest {

    private final HomeValueRuleValidator validator = new HomeValueRuleValidator();

    @Test
    void validate_shouldPass_whenLoanLessThanOrEqualHomeValue() {
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
    void validate_shouldThrowException_whenLoanExceedsHomeValue() {
        MortgageCheckRequest request = new MortgageCheckRequest(
                new BigDecimal("50000"),
                10,
                new BigDecimal("250000"),
                new BigDecimal("200000")
        );
        assertThatThrownBy(() -> validator.validate(request))
                .isInstanceOf(MortgageNotFeasibleException.class)
                .hasMessageContaining("home value");
    }
}