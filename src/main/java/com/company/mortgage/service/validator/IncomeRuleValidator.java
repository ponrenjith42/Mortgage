package com.company.mortgage.service.validator;

import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.exception.MortgageNotFeasibleException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class IncomeRuleValidator implements MortgageRuleValidator {

    @Override
    public void validate(MortgageCheckRequest request) {
        if (request.loanValue()
                .compareTo(request.income().multiply(BigDecimal.valueOf(4))) > 0) {
            log.error("Loan value exceeds 4 times the income, traceId:{}", MDC.get("traceId"));
            throw new MortgageNotFeasibleException(
                    "Loan value exceeds 4 times the income");
        }
    }
}