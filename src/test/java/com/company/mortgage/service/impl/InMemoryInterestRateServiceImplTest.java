package com.company.mortgage.service.impl;

import com.company.mortgage.exception.DuplicateInterestRateException;
import com.company.mortgage.exception.InterestRateNotFoundException;
import com.company.mortgage.repository.model.InterestRateEntity;
import com.company.mortgage.response.InterestRateResponse;
import com.company.mortgage.service.mapper.InterestRateMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.company.mortgage.util.TestData.getInterestRateEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InMemoryInterestRateServiceImplTest {

    @Mock
    private InterestRateMapper interestRateMapper;

    @InjectMocks
    private InMemoryInterestRateServiceImpl service;

    @Test
    void addRates_shouldStoreRatesSuccessfully() {
        service.addRates(List.of(getInterestRateEntity(10, "3.5")));

        InterestRateEntity result = service.getRateByMaturity(10);

        assertEquals(10, result.getMaturityPeriod());
        assertEquals("3.5", result.getInterestRate().toString());
    }

    @Test
    void addRates_shouldThrowExceptionOnDuplicateMaturity() {
        InterestRateEntity rate = getInterestRateEntity(10, "3.5");

        service.addRates(List.of(rate));

        DuplicateInterestRateException ex = assertThrows(
                DuplicateInterestRateException.class,
                () -> service.addRates(List.of(rate))
        );

        assertTrue(ex.getMessage().contains("Duplicate maturity period"));
    }

    @Test
    void getAllRates_shouldReturnAllRates() {
        service.addRates(List.of(getInterestRateEntity(10, "3.5")));

        when(interestRateMapper.toMortgageRateResponseList(any()))
                .thenReturn(List.of(new InterestRateResponse(
                        10,
                        new BigDecimal("3.5"),
                        LocalDateTime.of(2026, 01, 01, 11, 11, 11))));

        List<InterestRateResponse> result = service.getAllRates();

        assertEquals(1, result.size());
        verify(interestRateMapper, times(1))
                .toMortgageRateResponseList(any());
    }

    @Test
    void getAllRates_shouldThrowExceptionWhenEmpty() {
        assertThrows(
                InterestRateNotFoundException.class,
                () -> service.getAllRates()
        );
    }

    @Test
    void getRateByMaturity_shouldThrowExceptionWhenNotFound() {
        assertThrows(
                InterestRateNotFoundException.class,
                () -> service.getRateByMaturity(99)
        );
    }
}