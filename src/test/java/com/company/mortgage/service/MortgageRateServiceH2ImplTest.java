package com.company.mortgage.service;

import com.company.mortgage.repository.InterestRateRepository;
import com.company.mortgage.repository.model.MortgageRate;
import com.company.mortgage.service.exception.DuplicateInterestRateException;
import com.company.mortgage.service.exception.InterestRateNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MortgageRateServiceH2ImplTest {

    @Mock
    private InterestRateRepository interestRateRepository;

    @InjectMocks
    private MortgageRateServiceH2Impl service;

    private MortgageRate mortgageRate;

    @BeforeEach
    void setUp() {
        mortgageRate = MortgageRate.builder()
                .maturityPeriod(10)
                .interestRate(BigDecimal.valueOf(3.5))
                .lastUpdatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getAllRates_shouldReturnRates() {
        when(interestRateRepository.findAll()).thenReturn(List.of(mortgageRate));

        List<MortgageRate> result = service.getAllRates();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getMaturityPeriod()).isEqualTo(10);
        verify(interestRateRepository).findAll();
    }

    @Test
    void getRateByMaturity_shouldReturnRate_whenExists() {
        when(interestRateRepository.findById(10)).thenReturn(Optional.of(mortgageRate));

        MortgageRate result = service.getRateByMaturity(10);

        assertThat(result).isNotNull();
        assertThat(result.getInterestRate()).isEqualByComparingTo("3.5");
        verify(interestRateRepository).findById(10);
    }

    @Test
    void getRateByMaturity_shouldThrowException_whenNotFound() {
        when(interestRateRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getRateByMaturity(99))
                .isInstanceOf(InterestRateNotFoundException.class)
                .hasMessageContaining("99");

        verify(interestRateRepository).findById(99);
    }

    @Test
    void addRate_shouldSaveSuccessfully() {
        when(interestRateRepository.save(any(MortgageRate.class))).thenReturn(mortgageRate);

        service.addRate(10, BigDecimal.valueOf(3.5));

        verify(interestRateRepository, times(1)).save(any(MortgageRate.class));
    }

    @Test
    void addRate_shouldThrowDuplicateException_whenConstraintViolation() {
        when(interestRateRepository.save(any(MortgageRate.class)))
                .thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.addRate(10, BigDecimal.valueOf(3.5)))
                .isInstanceOf(DuplicateInterestRateException.class)
                .hasMessageContaining("10");

        verify(interestRateRepository, times(1)).save(any(MortgageRate.class));
    }
}