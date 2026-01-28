package com.company.mortgage.service;

import com.company.mortgage.repository.MortgageRateRepository;
import com.company.mortgage.repository.model.MortgageRate;
import com.company.mortgage.service.exception.InterestRateNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MortgageRateServiceH2ImplTest {

    @Mock
    private MortgageRateRepository mortgageRateRepository;

    @InjectMocks
    private MortgageRateServiceH2Impl service;

    private List<MortgageRate> mortgageRates;

    private MortgageRate mortgageRate1;

    private MortgageRate mortgageRate2;

    @BeforeEach
    void setup() {
        mortgageRate1 = MortgageRate.builder().maturityPeriod(10).interestRate(BigDecimal.valueOf(3.5)).build();
        mortgageRate2 = MortgageRate.builder().maturityPeriod(15).interestRate(BigDecimal.valueOf(4.0)).build();
        mortgageRates = List.of(mortgageRate1, mortgageRate2);
    }

    @Test
    void getAllRates_shouldReturnRates() {
        when(mortgageRateRepository.findAll()).thenReturn(mortgageRates);

        List<MortgageRate> result = service.getAllRates();

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getMaturityPeriod()).isEqualTo(10);
        verify(mortgageRateRepository).findAll();
    }

    @Test
    void getRateByMaturity_shouldReturnRate_whenExists() {
        when(mortgageRateRepository.findById(10)).thenReturn(Optional.of(mortgageRate1));

        MortgageRate result = service.getRateByMaturity(10);

        assertThat(result).isNotNull();
        assertThat(result.getInterestRate()).isEqualByComparingTo("3.5");
        verify(mortgageRateRepository).findById(10);
    }

    @Test
    void getRateByMaturity_shouldThrowException_whenNotFound() {
        when(mortgageRateRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getRateByMaturity(99))
                .isInstanceOf(InterestRateNotFoundException.class)
                .hasMessageContaining("99");

        verify(mortgageRateRepository).findById(99);
    }

    @Test
    void addRates_shouldSaveAllSuccessfully() {
        when(mortgageRateRepository.saveAll(mortgageRates)).thenReturn(mortgageRates);

        service.addRates(mortgageRates);

        verify(mortgageRateRepository, times(1)).saveAll(mortgageRates);
    }

    @Test
    void addRates_shouldThrowDuplicateException_whenConstraintViolation() {
        when(mortgageRateRepository.saveAll(mortgageRates))
                .thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.addRates(mortgageRates))
                .isInstanceOf(DataIntegrityViolationException.class);

        verify(mortgageRateRepository, times(1)).saveAll(mortgageRates);
    }
}