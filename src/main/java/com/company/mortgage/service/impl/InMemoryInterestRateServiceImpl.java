package com.company.mortgage.service.impl;

import com.company.mortgage.exception.DuplicateInterestRateException;
import com.company.mortgage.exception.InterestRateNotFoundException;
import com.company.mortgage.repository.model.InterestRateEntity;
import com.company.mortgage.response.InterestRateResponse;
import com.company.mortgage.service.InterestRateService;
import com.company.mortgage.service.mapper.InterestRateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Profile("in-memory")
@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryInterestRateServiceImpl implements InterestRateService {
    private final Map<Integer, InterestRateEntity> store = new ConcurrentHashMap<>();
    private final InterestRateMapper interestRateMapper;

    @Override
    public List<InterestRateResponse> getAllRates() {
        List<InterestRateEntity> interestRateEntityList =List.copyOf(store.values());
        if (interestRateEntityList.isEmpty()) {
            log.error("No interest rates found in the DB, traceId:{}", MDC.get("traceId"));
            throw new InterestRateNotFoundException("No interest rates found in the DB");
        }
        log.info("Fetching interest rates from source");
        return interestRateMapper.toMortgageRateResponseList(interestRateEntityList);
    }

    @Override
    public InterestRateEntity getRateByMaturity(int maturityPeriod) {
        return Optional.ofNullable(store.get(maturityPeriod)).orElseThrow(() -> {
            log.error("Interest rate not found for maturity: {} ,traceId:{}", maturityPeriod, MDC.get("traceId"));
            return new InterestRateNotFoundException(maturityPeriod);
        });
    }

    @Override
    public void addRates(List<InterestRateEntity> interestRateEntities) {
        for (InterestRateEntity rate : interestRateEntities) {
            if (store.containsKey(rate.getMaturityPeriod())) {
                throw new DuplicateInterestRateException(
                        "Duplicate maturity period: " , rate.getMaturityPeriod()
                );
            }
            store.put(rate.getMaturityPeriod(), rate);
        }
    }
}
