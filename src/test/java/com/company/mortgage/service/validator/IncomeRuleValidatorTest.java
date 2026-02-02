package com.company.mortgage.service.validator;

import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.exception.MortgageNotFeasibleException;
import com.company.mortgage.util.TestData;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IncomeRuleValidatorTest {

    private final IncomeRuleValidator validator = new IncomeRuleValidator();

    @Test
    void validate_shouldPass_whenLoanWithinIncomeLimit() {
        MortgageCheckRequest feasibleRequest = TestData.getMortgageCheckRequest("50000",10,"150000","200000");

        validator.validate(feasibleRequest);
    }

    @Test
    void validate_shouldThrowException_whenLoanExceedsIncomeLimit() {
        MortgageCheckRequest request = TestData.getMortgageCheckRequest("30000",10,"150000","200000");

        assertThatThrownBy(() -> validator.validate(request))
                .isInstanceOf(MortgageNotFeasibleException.class)
                .hasMessageContaining("income");
    }
}