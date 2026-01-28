package com.company.mortgage.service;

import com.company.mortgage.repository.model.MortgageRate;
import com.company.mortgage.service.exception.DuplicateInterestRateException;
import com.company.mortgage.service.exception.InterestRateNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
@ActiveProfiles("local")
class MortgageRateServiceImplTest {

    private MortgageRateServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new MortgageRateServiceImpl();
    }

    @Test
    void testAddAndGetRate() {
        service.addRate(10, BigDecimal.valueOf(3.5));

        MortgageRate rate = service.getRateByMaturity(10);

        assertThat(rate).isNotNull();
        assertThat(rate.getMaturityPeriod()).isEqualTo(10);
        assertThat(rate.getInterestRate()).isEqualByComparingTo("3.5");
        assertThat(rate.getLastUpdatedAt()).isNotNull();
    }

    @Test
    void testGetAllRates() {
        service.addRate(5, BigDecimal.valueOf(2.0));
        service.addRate(10, BigDecimal.valueOf(3.0));
        service.addRate(20, BigDecimal.valueOf(4.0));

        List<MortgageRate> allRates = service.getAllRates();

        assertThat(allRates).hasSize(3);
        assertThat(allRates).extracting(MortgageRate::getMaturityPeriod)
                .containsExactlyInAnyOrder(5, 10, 20);
    }

    @Test
    void testDuplicateRateThrowsException() {
        service.addRate(10, BigDecimal.valueOf(3.5));

        assertThatThrownBy(() -> service.addRate(10, BigDecimal.valueOf(4.0)))
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