package com.company.mortgage.service.impl;

import com.company.mortgage.repository.model.InterestRateEntity;
import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.response.MortgageCheckResponse;
import com.company.mortgage.exception.InterestRateNotFoundException;
import com.company.mortgage.exception.MortgageNotFeasibleException;
import com.company.mortgage.service.InterestRateService;
import com.company.mortgage.service.validator.MortgageRuleEngine;
import com.company.mortgage.util.TestData;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.company.mortgage.util.TestData.getInterestRateEntity;
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


    @Test
    void testCheckMortgage_Feasible() {
        MortgageCheckRequest feasibleRequest = TestData.getMortgageCheckRequest("50000",10,"150000","200000");
        InterestRateEntity rate = getInterestRateEntity(10,"3.5");

        when(interestRateService.getRateByMaturity(10)).thenReturn(rate);

        MortgageCheckResponse response =
                mortgageCheckService.checkMortgage(feasibleRequest);

        assertThat(response.feasible()).isTrue();
        assertThat(response.monthlyCost()).isPositive();
        verify(interestRateService).getRateByMaturity(10);
    }

    @Test
    void testCheckMortgage_InfeasibleIncome() {
        MortgageCheckRequest incomeFailRequest = TestData.getMortgageCheckRequest("30000",10,"150000","200000");

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
        MortgageCheckRequest homeValueFailRequest = TestData.getMortgageCheckRequest("100000",10,"250000","200000");

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
        MortgageCheckRequest feasibleRequest = TestData.getMortgageCheckRequest("50000",10,"150000","200000");
        when(interestRateService.getRateByMaturity(10))
                .thenThrow(new InterestRateNotFoundException(10));

        assertThatThrownBy(() ->
                mortgageCheckService.checkMortgage(feasibleRequest))
                .isInstanceOf(InterestRateNotFoundException.class)
                .hasMessageContaining("10");
    }
}