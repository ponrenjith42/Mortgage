package com.company.mortgage.service;

import com.company.mortgage.repository.model.InterestRateEntity;
import com.company.mortgage.service.exception.DuplicateInterestRateException;
import com.company.mortgage.service.exception.InterestRateNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
@ActiveProfiles("local")
class InterestRateServiceImplTest {

    private InterestRateService service;

    private InterestRateEntity interestRateEntity1;

    private InterestRateEntity interestRateEntity2;

    private List<InterestRateEntity> interestRateEntities;

    @BeforeEach
    void setUp() {
        service = new InterestRateServiceImpl();
        interestRateEntity1 = InterestRateEntity.builder().maturityPeriod(10).interestRate(BigDecimal.valueOf(3.5)).build();
        interestRateEntity2 = InterestRateEntity.builder().maturityPeriod(15).interestRate(BigDecimal.valueOf(4.0)).build();
        interestRateEntities = List.of(interestRateEntity1, interestRateEntity2);
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
        List<InterestRateEntity> allRates = service.getAllRates();

        assertThat(allRates).hasSize(2);
        assertThat(allRates).extracting(InterestRateEntity::getMaturityPeriod)
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