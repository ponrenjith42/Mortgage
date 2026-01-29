package com.company.mortgage.service;

import com.company.mortgage.repository.model.InterestRateEntity;
import com.company.mortgage.service.exception.DuplicateInterestRateException;
import com.company.mortgage.service.exception.InterestRateNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Profile("local")
@RequiredArgsConstructor
@Slf4j
public class InterestRateServiceImpl implements InterestRateService {

    private final Map<Integer, InterestRateEntity> mortgageRateMap = new HashMap<>();

    public List<InterestRateEntity> getAllRates() {
        List<InterestRateEntity> interestRateEntityList = List.copyOf(mortgageRateMap.values());
        if (interestRateEntityList.isEmpty()) {
            log.error("No interest rates found in the DB");
            throw new InterestRateNotFoundException("No interest rates found in the DB");
        }
        return interestRateEntityList;
    }

    public InterestRateEntity getRateByMaturity(int maturity) {
        InterestRateEntity rate = mortgageRateMap.get(maturity);
        if (rate == null) {
            throw new InterestRateNotFoundException(maturity);
        }
        return rate;
    }

    @Override
    public void addRates(List<InterestRateEntity> interestRateEntities) {
        for (InterestRateEntity rate : interestRateEntities) {
            if (mortgageRateMap.putIfAbsent(rate.getMaturityPeriod(), rate) != null) {
                log.error("Failed to add mortgage rate for maturity period {}: duplicate entry)", rate.getMaturityPeriod());
                throw new DuplicateInterestRateException(rate.getMaturityPeriod());
            }
        }
    }
}