package com.company.mortgage.service.validator;

import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.service.exception.MortgageNotFeasibleException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class IncomeRuleValidator implements MortgageRuleValidator {

    @Override
    public void validate(MortgageCheckRequest request) {
        if (request.loanValue()
                .compareTo(request.income().multiply(BigDecimal.valueOf(4))) > 0) {
            throw new MortgageNotFeasibleException(
                    "Loan value exceeds 4 times the income");
        }
    }
}