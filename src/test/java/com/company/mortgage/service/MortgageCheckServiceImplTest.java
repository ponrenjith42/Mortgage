package com.company.mortgage.service;

import com.company.mortgage.repository.model.InterestRateEntity;
import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.response.MortgageCheckResponse;
import com.company.mortgage.service.exception.InterestRateNotFoundException;
import com.company.mortgage.service.exception.MortgageNotFeasibleException;
import com.company.mortgage.service.validator.MortgageRuleEngine;
import org.junit.jupiter.api.BeforeAll;
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
    private InterestRateService interestRateService;
    @Mock
    private MortgageRuleEngine mortgageRuleEngine;
    @InjectMocks
    private MortgageCheckServiceImpl mortgageCheckService;

    private static MortgageCheckRequest feasibleRequest;
    private static MortgageCheckRequest incomeFailRequest;
    private static MortgageCheckRequest homeValueFailRequest;

    @BeforeAll
    static void setup() {
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
        InterestRateEntity rate =
                new InterestRateEntity(10, new BigDecimal("3.5"), LocalDateTime.now());

        when(interestRateService.getRateByMaturity(10)).thenReturn(rate);

        MortgageCheckResponse response =
                mortgageCheckService.checkMortgage(feasibleRequest);

        assertThat(response.feasible()).isTrue();
        assertThat(response.monthlyCost()).isPositive();
        verify(interestRateService).getRateByMaturity(10);
    }

    @Test
    void testCheckMortgage_InfeasibleIncome() {
        doThrow(new MortgageNotFeasibleException("Loan value exceeds income"))
                .when(mortgageRuleEngine)
                .validate(incomeFailRequest);

        assertThatThrownBy(() ->
                mortgageCheckService.checkMortgage(incomeFailRequest))
                .isInstanceOf(MortgageNotFeasibleException.class)
                .hasMessageContaining("income");

        verifyNoInteractions(interestRateService);
    }

    @Test
    void testCheckMortgage_InfeasibleHomeValue() {
        doThrow(new MortgageNotFeasibleException("Loan value exceeds home value"))
                .when(mortgageRuleEngine)
                .validate(homeValueFailRequest);

        assertThatThrownBy(() ->
                mortgageCheckService.checkMortgage(homeValueFailRequest))
                .isInstanceOf(MortgageNotFeasibleException.class)
                .hasMessageContaining("home value");

        verifyNoInteractions(interestRateService);
    }

    @Test
    void testCheckMortgage_RateNotFound() {
        when(interestRateService.getRateByMaturity(10))
                .thenThrow(new InterestRateNotFoundException(10));

        assertThatThrownBy(() ->
                mortgageCheckService.checkMortgage(feasibleRequest))
                .isInstanceOf(InterestRateNotFoundException.class)
                .hasMessageContaining("10");
    }
}