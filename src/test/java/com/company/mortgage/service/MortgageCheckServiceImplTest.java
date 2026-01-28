package com.company.mortgage.service;

import com.company.mortgage.repository.model.MortgageRate;
import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.response.MortgageCheckResponse;
import com.company.mortgage.service.exception.InterestRateNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class MortgageCheckServiceImplTest {

    @Mock
    private MortgageRateService mortgageRateService;

    @InjectMocks
    private MortgageCheckServiceImpl mortgageCheckService;

    private MortgageCheckRequest feasibleRequest;
    private MortgageCheckRequest incomeFailRequest;
    private MortgageCheckRequest homeValueFailRequest;

    @BeforeEach
    void setup() {
        feasibleRequest = new MortgageCheckRequest(
                new BigDecimal("50000"),
                10,
                new BigDecimal("150000"),
                new BigDecimal("200000")
        );

        incomeFailRequest = new MortgageCheckRequest(
                new BigDecimal("30000"),
                10,
                new BigDecimal("150000"),
                new BigDecimal("200000")
        );

        homeValueFailRequest = new MortgageCheckRequest(
                new BigDecimal("100000"),
                10,
                new BigDecimal("250000"),
                new BigDecimal("200000")
        );
    }

    @Test
    void testCheckMortgage_Feasible() {
        MortgageRate rate = new MortgageRate(10, new BigDecimal("3.5"), LocalDateTime.now());
        when(mortgageRateService.getRateByMaturity(10)).thenReturn(rate);

        MortgageCheckResponse response = mortgageCheckService.checkMortgage(feasibleRequest);

        assertThat(response.feasible()).isTrue();
        assertThat(response.monthlyCost()).isPositive();
        verify(mortgageRateService, times(1)).getRateByMaturity(10);
    }

    @Test
    void testCheckMortgage_InfeasibleIncome() {
        MortgageCheckResponse response = mortgageCheckService.checkMortgage(incomeFailRequest);

        assertThat(response.feasible()).isFalse();
        assertThat(response.monthlyCost()).isEqualByComparingTo(BigDecimal.ZERO);
        verifyNoInteractions(mortgageRateService);
    }

    @Test
    void testCheckMortgage_InfeasibleHomeValue() {
        MortgageCheckResponse response = mortgageCheckService.checkMortgage(homeValueFailRequest);

        assertThat(response.feasible()).isFalse();
        assertThat(response.monthlyCost()).isEqualByComparingTo(BigDecimal.ZERO);
        verifyNoInteractions(mortgageRateService);
    }

    @Test
    void testCheckMortgage_RateNotFound() {
        when(mortgageRateService.getRateByMaturity(10))
                .thenThrow(new InterestRateNotFoundException(10));

        assertThatThrownBy(() -> mortgageCheckService.checkMortgage(feasibleRequest))
                .isInstanceOf(InterestRateNotFoundException.class)
                .hasMessageContaining("10");
    }
}