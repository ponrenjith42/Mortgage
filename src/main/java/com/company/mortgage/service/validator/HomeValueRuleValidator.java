package com.company.mortgage.service.validator;

import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.service.exception.MortgageNotFeasibleException;
import org.springframework.stereotype.Component;

@Component
public class HomeValueRuleValidator implements MortgageRuleValidator {

    @Override
    public void validate(MortgageCheckRequest request) {
        if (request.loanValue().compareTo(request.homeValue()) > 0) {
            throw new MortgageNotFeasibleException(
                    "Loan value exceeds home value");
        }
    }
}