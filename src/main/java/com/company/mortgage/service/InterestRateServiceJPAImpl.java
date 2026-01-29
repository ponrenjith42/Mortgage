package com.company.mortgage.service;

import com.company.mortgage.repository.InterestRateRepository;
import com.company.mortgage.repository.model.InterestRateEntity;
import com.company.mortgage.response.InterestRateResponse;
import com.company.mortgage.service.exception.InterestRateNotFoundException;
import com.company.mortgage.service.mapper.InterestRateMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile({"h2", "cache"})
@RequiredArgsConstructor
@Slf4j
public class InterestRateServiceJPAImpl implements InterestRateService {

    private final InterestRateRepository interestRateRepository;
    private final InterestRateMapper interestRateMapper;

    @Override
    @Cacheable(value = "interestRates")
    public List<InterestRateResponse> getAllRates() {
        List<InterestRateEntity> interestRateEntityList = interestRateRepository.findAll();
        if (interestRateEntityList.isEmpty()) {
            log.error("No interest rates found in the DB");
            throw new InterestRateNotFoundException("No interest rates found in the DB");
        }
        log.info("Fetching interest rates from source");
        return interestRateMapper.toMortgageRateResponseList(interestRateEntityList);
    }

    @Override
    @Cacheable(value = "interestRates", key = "#maturity")
    public InterestRateEntity getRateByMaturity(int maturity) {
        return interestRateRepository.findById(maturity).orElseThrow(() -> {
            log.error("Interest rate not found for maturity: {}", maturity);
            return new InterestRateNotFoundException(maturity);
        });
    }

    @Override
    @Transactional
    @CacheEvict(value = "interestRates", allEntries = true)
    public void addRates(List<InterestRateEntity> rates) {
        interestRateRepository.saveAll(rates);
        log.info("Added {} mortgage rates in bulk", rates.size());
    }

}