package com.company.mortgage.service;

import com.company.mortgage.repository.model.MortgageRate;
import com.company.mortgage.service.exception.DuplicateInterestRateException;
import com.company.mortgage.service.exception.InterestRateNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Profile("local")
public class MortgageRateServiceImpl implements MortgageRateService {

    private final Map<Integer, MortgageRate> mortgageRateMap = new HashMap<>();

    public List<MortgageRate> getAllRates() {
        return List.copyOf(mortgageRateMap.values());
    }

    public MortgageRate getRateByMaturity(int maturity) {
        MortgageRate rate = mortgageRateMap.get(maturity);
        if (rate == null) {
            throw new InterestRateNotFoundException(maturity);
        }
        return rate;
    }

    public void addRate(int maturity, BigDecimal rate) {
        if (mortgageRateMap.containsKey(maturity)) {
            throw new DuplicateInterestRateException(maturity);
        }
        mortgageRateMap.put(maturity, MortgageRate.builder()
                .maturityPeriod(maturity)
                .interestRate(rate)
                .lastUpdatedAt(LocalDateTime.now())
                .build());
    }
}