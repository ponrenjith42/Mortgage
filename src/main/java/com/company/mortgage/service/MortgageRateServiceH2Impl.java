package com.company.mortgage.service;

import com.company.mortgage.repository.MortgageRateRepository;
import com.company.mortgage.repository.model.MortgageRate;
import com.company.mortgage.service.exception.InterestRateNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("h2")
@RequiredArgsConstructor
@Slf4j
public class MortgageRateServiceH2Impl implements MortgageRateService {

    private final MortgageRateRepository mortgageRateRepository;

    @Override
    public List<MortgageRate> getAllRates() {
        List<MortgageRate> mortgageRateList = mortgageRateRepository.findAll();
        if (mortgageRateList.isEmpty()) {
            log.error("No interest rates found in the DB");
            throw new InterestRateNotFoundException("No interest rates found in the DB");
        }
        return mortgageRateList;
    }

    @Override
    public MortgageRate getRateByMaturity(int maturity) {
        return mortgageRateRepository.findById(maturity).orElseThrow(() -> {
            log.error("Interest rate not found for maturity: {}", maturity);
            return new InterestRateNotFoundException(maturity);
        });
    }

    @Override
    @Transactional
    public void addRates(List<MortgageRate> rates) {
        mortgageRateRepository.saveAll(rates);
        log.info("Added {} mortgage rates in bulk", rates.size());
    }

}