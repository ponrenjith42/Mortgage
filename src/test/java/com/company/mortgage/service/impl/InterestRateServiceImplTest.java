package com.company.mortgage.service.impl;

import com.company.mortgage.repository.InterestRateRepository;
import com.company.mortgage.repository.model.InterestRateEntity;
import com.company.mortgage.response.InterestRateResponse;
import com.company.mortgage.exception.DuplicateInterestRateException;
import com.company.mortgage.exception.InterestRateNotFoundException;
import com.company.mortgage.service.mapper.InterestRateMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.company.mortgage.util.TestData.getInterestRateEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InterestRateServiceImplTest {

    @InjectMocks
    private InterestRateServiceImpl service;

    @Mock
    private InterestRateRepository repository;

    @Mock
    private InterestRateMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllRates_ReturnsMappedResponses() {

        when(repository.findAll()).thenReturn(List.of(getInterestRateEntity(5,"2.0")));
        InterestRateResponse response = new InterestRateResponse(
                5,
                BigDecimal.valueOf(2.0),
                LocalDateTime.of(2026,01,01,11,11,11));
        when(mapper.toMortgageRateResponseList(any())).thenReturn(List.of(response));

        List<InterestRateResponse> result = service.getAllRates();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(repository).findAll();
        verify(mapper).toMortgageRateResponseList(any());
    }

    @Test
    void testGetAllRates_ThrowsWhenEmpty() {
        when(repository.findAll()).thenReturn(List.of());

        InterestRateNotFoundException ex = assertThrows(
                InterestRateNotFoundException.class,
                () -> service.getAllRates()
        );
        assertEquals("No interest rates found in the DB", ex.getMessage());
    }

    @Test
    void testGetRateByMaturity_Found() {
        when(repository.findByMaturityPeriod(5)).thenReturn(Optional.of(getInterestRateEntity(5,"2.0")));

        InterestRateEntity result = service.getRateByMaturity(5);

        assertNotNull(result);
        assertEquals(5, result.getMaturityPeriod());
        verify(repository).findByMaturityPeriod(5);
    }

    @Test
    void testGetRateByMaturity_NotFound() {
        when(repository.findByMaturityPeriod(10)).thenReturn(Optional.empty());

        InterestRateNotFoundException ex = assertThrows(
                InterestRateNotFoundException.class,
                () -> service.getRateByMaturity(10)
        );

        assertEquals(10, ex.getMaturityPeriod());
    }

    @Test
    void testAddRates_Success() {
        when(repository.saveAll(anyList())).thenReturn(List.of(getInterestRateEntity(5,"2.0")));

        assertDoesNotThrow(() -> service.addRates(List.of(getInterestRateEntity(5,"2.0"))));

        verify(repository).saveAll(anyList());
    }

    @Test
    void testAddRates_Duplicate_Throws() {
        when(repository.saveAll(anyList())).thenThrow(new org.springframework.dao.DataIntegrityViolationException("Duplicate"));

        DuplicateInterestRateException ex = assertThrows(
                DuplicateInterestRateException.class,
                () -> service.addRates(List.of(getInterestRateEntity(5,"2.0")))
        );

        assertEquals("Duplicate maturity period detected", ex.getMessage());
    }
}