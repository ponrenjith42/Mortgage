package com.company.mortgage.service;

import com.company.mortgage.repository.model.MortgageRate;
import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.response.MortgageCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class MortgageCheckServiceImpl implements MortgageCheckService {

    private final MortgageRateService mortgageRateService;

    public MortgageCheckResponse checkMortgage(MortgageCheckRequest request) {

        BigDecimal monthlyCosts = BigDecimal.ZERO;
        boolean feasible = checkFeasibility(request);
        if (feasible) {
            MortgageRate rate = mortgageRateService.getRateByMaturity(request.maturityPeriod());
            monthlyCosts = calculateMonthlyCost(
                    request.loanValue(),
                    rate.getInterestRate(),
                    request.maturityPeriod()
            );
        }
        return new MortgageCheckResponse(feasible, monthlyCosts);
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

    private boolean checkFeasibility(MortgageCheckRequest request) {
        return request.loanValue().compareTo(request.income().multiply(BigDecimal.valueOf(4))) <= 0
                && request.loanValue().compareTo(request.homeValue()) <= 0;
    }
}
