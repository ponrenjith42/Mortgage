package com.company.mortgage.service;

import com.company.mortgage.repository.InterestRateRepository;
import com.company.mortgage.repository.model.InterestRateEntity;
import com.company.mortgage.response.InterestRateResponse;
import com.company.mortgage.service.exception.InterestRateNotFoundException;
import com.company.mortgage.service.mapper.InterestRateMapper;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterestRateServiceJPAImplTest {

    @Mock
    private InterestRateRepository interestRateRepository;

    @Mock
    private InterestRateMapper interestRateMapper;

    @InjectMocks
    private InterestRateServiceJPAImpl service;

    private List<InterestRateEntity> interestRateEntities;

    private InterestRateEntity interestRateEntity1;

    private InterestRateEntity interestRateEntity2;

    @BeforeEach
    void setup() {
        interestRateEntity1 = InterestRateEntity.builder().maturityPeriod(10).interestRate(BigDecimal.valueOf(3.5)).build();
        interestRateEntity2 = InterestRateEntity.builder().maturityPeriod(15).interestRate(BigDecimal.valueOf(4.0)).build();
        interestRateEntities = List.of(interestRateEntity1, interestRateEntity2);
    }

    @Test
    void getAllRates_shouldReturnRates() {
        List<InterestRateResponse> interestRateResponses = List.of(
                new InterestRateResponse(10, BigDecimal.valueOf(3.0), LocalDateTime.now()),
                new InterestRateResponse(15, BigDecimal.valueOf(3.5), LocalDateTime.now())
        );

        when(interestRateRepository.findAll()).thenReturn(interestRateEntities);
        when(interestRateMapper.toMortgageRateResponseList(interestRateEntities))
                .thenReturn(interestRateResponses);

        List<InterestRateResponse> result = service.getAllRates();

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().maturityPeriod()).isEqualTo(10);
        verify(interestRateRepository).findAll();
    }

    @Test
    void getRateByMaturity_shouldReturnRate_whenExists() {
        when(interestRateRepository.findById(10)).thenReturn(Optional.of(interestRateEntity1));

        InterestRateEntity result = service.getRateByMaturity(10);

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
    void addRates_shouldSaveAllSuccessfully() {
        when(interestRateRepository.saveAll(interestRateEntities)).thenReturn(interestRateEntities);

        service.addRates(interestRateEntities);

        verify(interestRateRepository, times(1)).saveAll(interestRateEntities);
    }

    @Test
    void addRates_shouldThrowDuplicateException_whenConstraintViolation() {
        when(interestRateRepository.saveAll(interestRateEntities))
                .thenThrow(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> service.addRates(interestRateEntities))
                .isInstanceOf(DataIntegrityViolationException.class);

        verify(interestRateRepository, times(1)).saveAll(interestRateEntities);
    }
}