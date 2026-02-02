package com.company.mortgage.service.validator;

import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.exception.MortgageNotFeasibleException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HomeValueRuleValidator implements MortgageRuleValidator {

    @Override
    public void validate(MortgageCheckRequest request) {
        if (request.loanValue().compareTo(request.homeValue()) > 0) {
            log.error("Loan value exceeds home value, traceId:{}", MDC.get("traceId"));
            throw new MortgageNotFeasibleException(
                    "Loan value exceeds home value");
        }
    }
}