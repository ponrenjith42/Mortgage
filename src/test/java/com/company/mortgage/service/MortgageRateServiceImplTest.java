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

    private MortgageRateService service;

    private MortgageRate mortgageRate1;

    private MortgageRate mortgageRate2;

    private List<MortgageRate> mortgageRates;

    @BeforeEach
    void setUp() {
        service = new MortgageRateServiceImpl();
        mortgageRate1 = MortgageRate.builder().maturityPeriod(10).interestRate(BigDecimal.valueOf(3.5)).build();
        mortgageRate2 = MortgageRate.builder().maturityPeriod(15).interestRate(BigDecimal.valueOf(4.0)).build();
        mortgageRates = List.of(mortgageRate1, mortgageRate2);
        service.addRates(mortgageRates);
    }

    @Test
    void testAddAndGetRate() {

        MortgageRate rate = service.getRateByMaturity(10);

        assertThat(rate).isNotNull();
        assertThat(rate.getMaturityPeriod()).isEqualTo(10);
        assertThat(rate.getInterestRate()).isEqualByComparingTo("3.5");
        assertThat(rate.getLastUpdatedAt()).isNotNull();
    }

    @Test
    void testGetAllRates() {
        List<MortgageRate> allRates = service.getAllRates();

        assertThat(allRates).hasSize(2);
        assertThat(allRates).extracting(MortgageRate::getMaturityPeriod)
                .containsExactlyInAnyOrder(10, 15);
    }

    @Test
    void testDuplicateRateThrowsException() {
        List<MortgageRate> mortgageRateList = List.of(mortgageRate1,mortgageRate1);

        assertThatThrownBy(() -> service.addRates(mortgageRateList))
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