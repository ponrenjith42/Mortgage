package com.company.mortgage.service;

import com.company.mortgage.repository.InterestRateRepository;
import com.company.mortgage.repository.model.MortgageRate;
import com.company.mortgage.service.exception.DuplicateInterestRateException;
import com.company.mortgage.service.exception.InterestRateNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Profile("h2")
@RequiredArgsConstructor
@Slf4j
public class MortgageRateServiceH2Impl implements MortgageRateService {

    private final InterestRateRepository interestRateRepository;

    @Override
    public List<MortgageRate> getAllRates() {
        return interestRateRepository.findAll();
    }

    @Override
    public MortgageRate getRateByMaturity(int maturity) {
        return interestRateRepository.findById(maturity).orElseThrow(() -> {
            log.error("Interest rate not found for maturity: {}", maturity);
            return new InterestRateNotFoundException(maturity);
        });
    }

    @Override
    public void addRate(int maturity, BigDecimal rate) {
        try {
            interestRateRepository.save(MortgageRate.builder()
                    .maturityPeriod(maturity)
                    .interestRate(rate)
                    .build());
            log.info("Added interest rate: maturity={} rate={}", maturity, rate);
        } catch (DataIntegrityViolationException ex) {
            log.error("Failed to add interest rate for maturity {} (duplicate)", maturity, ex);
            throw new DuplicateInterestRateException(maturity);
        }
    }
}