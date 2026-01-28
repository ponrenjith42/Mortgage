package com.company.mortgage.service;

import com.company.mortgage.repository.model.MortgageRate;

import java.util.List;

public interface MortgageRateService {

    List<MortgageRate> getAllRates();

    MortgageRate getRateByMaturity(int maturityPeriod);

    void addRates(List<MortgageRate> mortgageRates);
}
