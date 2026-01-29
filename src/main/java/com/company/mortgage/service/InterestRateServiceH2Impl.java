package com.company.mortgage.service;

import com.company.mortgage.repository.InterestRateRepository;
import com.company.mortgage.repository.model.InterestRateEntity;
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
public class InterestRateServiceH2Impl implements InterestRateService {

    private final InterestRateRepository interestRateRepository;

    @Override
    public List<InterestRateEntity> getAllRates() {
        List<InterestRateEntity> interestRateEntityList = interestRateRepository.findAll();
        if (interestRateEntityList.isEmpty()) {
            log.error("No interest rates found in the DB");
            throw new InterestRateNotFoundException("No interest rates found in the DB");
        }
        return interestRateEntityList;
    }

    @Override
    public InterestRateEntity getRateByMaturity(int maturity) {
        return interestRateRepository.findById(maturity).orElseThrow(() -> {
            log.error("Interest rate not found for maturity: {}", maturity);
            return new InterestRateNotFoundException(maturity);
        });
    }

    @Override
    @Transactional
    public void addRates(List<InterestRateEntity> rates) {
        interestRateRepository.saveAll(rates);
        log.info("Added {} mortgage rates in bulk", rates.size());
    }

}