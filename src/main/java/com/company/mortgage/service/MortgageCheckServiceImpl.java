package com.company.mortgage.service;

import com.company.mortgage.repository.model.MortgageRate;
import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.response.MortgageCheckResponse;
import com.company.mortgage.service.validator.MortgageRuleEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class MortgageCheckServiceImpl implements MortgageCheckService {

    private final MortgageRateService mortgageRateService;
    private final MortgageRuleEngine mortgageRuleEngine;

    public MortgageCheckResponse checkMortgage(MortgageCheckRequest request) {
        mortgageRuleEngine.validate(request);
        MortgageRate mortgageRate = mortgageRateService.getRateByMaturity(request.maturityPeriod());
        BigDecimal monthlyCost = calculateMonthlyCost(
                request.loanValue(),
                mortgageRate.getInterestRate(),
                request.maturityPeriod()
        );
        return new MortgageCheckResponse(true, monthlyCost);
    }

    private BigDecimal calculateMonthlyCost(BigDecimal loan, BigDecimal annualRate, int years) {
        BigDecimal monthlyRate =
                annualRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                        .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        int months = years * 12;
        return loan.multiply(monthlyRate)
                .multiply(
                        BigDecimal.ONE.add(monthlyRate).pow(months)
                )
                .divide(
                        BigDecimal.ONE.add(monthlyRate).pow(months).subtract(BigDecimal.ONE),
                        2,
                        RoundingMode.HALF_UP
                );
    }
}
