package com.company.mortgage.service.validator;

import com.company.mortgage.request.MortgageCheckRequest;

public interface MortgageRuleValidator {
    void validate(MortgageCheckRequest request);
}