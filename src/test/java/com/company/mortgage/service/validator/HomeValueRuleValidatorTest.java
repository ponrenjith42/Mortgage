package com.company.mortgage.service.validator;

import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.exception.MortgageNotFeasibleException;
import com.company.mortgage.util.TestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HomeValueRuleValidatorTest {

    private final HomeValueRuleValidator validator = new HomeValueRuleValidator();

    @Test
    void validate_shouldPass_whenLoanLessThanOrEqualHomeValue() {
        MortgageCheckRequest feasibleRequest = TestData.getMortgageCheckRequest("50000",10,"150000","200000");

        validator.validate(feasibleRequest);
    }

    @Test
    void validate_shouldThrowException_whenLoanExceedsHomeValue() {
        MortgageCheckRequest inFeasibleRequest = TestData.getMortgageCheckRequest("50000",10,"250000","200000");

        assertThatThrownBy(() -> validator.validate(inFeasibleRequest))
                .isInstanceOf(MortgageNotFeasibleException.class)
                .hasMessageContaining("home value");
    }
}