package com.company.mortgage.service;

import com.company.mortgage.exception.InterestRateSaveException;
import com.company.mortgage.repository.InterestRateRepository;
import com.company.mortgage.repository.model.InterestRateEntity;
import com.company.mortgage.response.InterestRateResponse;
import com.company.mortgage.exception.DuplicateInterestRateException;
import com.company.mortgage.exception.InterestRateNotFoundException;
import com.company.mortgage.service.mapper.InterestRateMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterestRateServiceImpl implements InterestRateService {

    private final InterestRateRepository interestRateRepository;
    private final InterestRateMapper interestRateMapper;

    @Override
    @Cacheable(value = "interestRates", key = "'allRates'")
    public List<InterestRateResponse> getAllRates() {
        List<InterestRateEntity> interestRateEntityList = interestRateRepository.findAll();
        if (interestRateEntityList.isEmpty()) {
            log.error("No interest rates found in the DB, traceId:{}", MDC.get("traceId"));
            throw new InterestRateNotFoundException("No interest rates found in the DB");
        }
        log.info("Fetching interest rates from source");
        return interestRateMapper.toMortgageRateResponseList(interestRateEntityList);
    }

    @Override
    @Cacheable(value = "interestRates", key = "#maturity")
    public InterestRateEntity getRateByMaturity(int maturity) {
        return interestRateRepository.findByMaturityPeriod(maturity).orElseThrow(() -> {
            log.error("Interest rate not found for maturity: {} ,traceId:{}", maturity, MDC.get("traceId"));
            return new InterestRateNotFoundException(maturity);
        });
    }

    @Override
    @CacheEvict(value = "interestRates", allEntries = true, beforeInvocation = true)
    @Transactional
   public void addRates(List<InterestRateEntity> rates) {
        try {
            interestRateRepository.saveAll(rates);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate or constraint violation while saving interest rates ", e);
            throw new DuplicateInterestRateException("Duplicate maturity period detected", e);
        } catch (Exception e) {
            log.error("Failed to save interest rates", e);
            throw new InterestRateSaveException("Failed to save interest rates", e);
        }
    }
}