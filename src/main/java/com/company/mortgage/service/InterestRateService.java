package com.company.mortgage.service;

import com.company.mortgage.repository.model.InterestRateEntity;

import java.util.List;

public interface InterestRateService {

    List<InterestRateEntity> getAllRates();

    InterestRateEntity getRateByMaturity(int maturityPeriod);

    void addRates(List<InterestRateEntity> interestRateEntities);
}
