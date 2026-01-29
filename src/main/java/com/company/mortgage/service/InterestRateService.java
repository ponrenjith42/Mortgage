package com.company.mortgage.service;

import com.company.mortgage.repository.model.InterestRateEntity;
import com.company.mortgage.response.InterestRateResponse;

import java.util.List;

public interface InterestRateService {

    List<InterestRateResponse> getAllRates();

    InterestRateEntity getRateByMaturity(int maturityPeriod);

    void addRates(List<InterestRateEntity> interestRateEntities);
}
