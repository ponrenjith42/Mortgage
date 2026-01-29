package com.company.mortgage.service;

import com.company.mortgage.repository.model.InterestRateEntity;
import com.company.mortgage.response.InterestRateResponse;
import com.company.mortgage.service.exception.DuplicateInterestRateException;
import com.company.mortgage.service.exception.InterestRateNotFoundException;
import com.company.mortgage.service.mapper.InterestRateMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterestRateServiceImplTest {

    @InjectMocks
    private InterestRateServiceImpl service;

    @Mock
    private InterestRateMapper interestRateMapper;

    private InterestRateEntity interestRateEntity1;

    private InterestRateEntity interestRateEntity2;

    private List<InterestRateEntity> interestRateEntities;

    private List<InterestRateResponse> interestRateResponses;

    @BeforeEach
    void setUp() {
        interestRateEntity1 = InterestRateEntity.builder().maturityPeriod(10).interestRate(BigDecimal.valueOf(3.5)).build();
        interestRateEntity2 = InterestRateEntity.builder().maturityPeriod(15).interestRate(BigDecimal.valueOf(4.0)).build();
        interestRateEntities = List.of(interestRateEntity1, interestRateEntity2);

        interestRateResponses = List.of(
                new InterestRateResponse(10, BigDecimal.valueOf(3.5), interestRateEntity1.getLastUpdatedAt()),
                new InterestRateResponse(15, BigDecimal.valueOf(4.0), interestRateEntity2.getLastUpdatedAt())
        );
        service.addRates(interestRateEntities);
    }

    @Test
    void testAddAndGetRate() {
        InterestRateEntity rate = service.getRateByMaturity(10);

        assertThat(rate).isNotNull();
        assertThat(rate.getMaturityPeriod()).isEqualTo(10);
        assertThat(rate.getInterestRate()).isEqualByComparingTo("3.5");
        assertThat(rate.getLastUpdatedAt()).isNotNull();
    }

    @Test
    void testGetAllRates() {
        when(interestRateMapper.toMortgageRateResponseList(interestRateEntities))
                .thenReturn(interestRateResponses);

        List<InterestRateResponse> allRates = service.getAllRates();

        assertThat(allRates).hasSize(2);
        assertThat(allRates).extracting(InterestRateResponse::maturityPeriod)
                .containsExactlyInAnyOrder(10, 15);
    }

    @Test
    void testDuplicateRateThrowsException() {
        List<InterestRateEntity> interestRateEntityList = List.of(interestRateEntity1, interestRateEntity1);

        assertThatThrownBy(() -> service.addRates(interestRateEntityList))
                .isInstanceOf(DuplicateInterestRateException.class)
                .hasMessageContaining("10");
    }

    @Test
    void testGetNonExistingRateThrowsException() {
        assertThatThrownBy(() -> service.getRateByMaturity(99))
                .isInstanceOf(InterestRateNotFoundException.class)
                .hasMessageContaining("99");
    }
}