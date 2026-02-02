package com.company.mortgage.util;

import com.company.mortgage.repository.model.InterestRateEntity;
import com.company.mortgage.request.MortgageCheckRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestData {

    public static MortgageCheckRequest getMortgageCheckRequest(
            String incomeStr, int maturity, String loanValueStr, String homeValueStr) {

        BigDecimal income = (incomeStr != null) ? new BigDecimal(incomeStr) : null;
        BigDecimal loanValue = (loanValueStr != null) ? new BigDecimal(loanValueStr) : null;
        BigDecimal homeValue = (homeValueStr != null) ? new BigDecimal(homeValueStr) : null;

        return new MortgageCheckRequest(
                income,
                maturity,
                loanValue,
                homeValue
        );
    }

    public static InterestRateEntity getInterestRateEntity(int maturityPeriod,String interestRate) {
        return InterestRateEntity.builder()
                .maturityPeriod(maturityPeriod)
                .interestRate(new BigDecimal(interestRate))
                .lastUpdate(LocalDateTime.of(2026,01,01,11,11,11))
                .build();
    }
}
