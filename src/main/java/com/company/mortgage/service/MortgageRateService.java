package com.company.mortgage.service;

import com.company.mortgage.repository.model.MortgageRate;

import java.math.BigDecimal;
import java.util.List;

public interface MortgageRateService {

    List<MortgageRate> getAllRates();

    MortgageRate getRateByMaturity(int maturityPeriod);

    void addRate(int maturityPeriod, BigDecimal interestRate);
}
