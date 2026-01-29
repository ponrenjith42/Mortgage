package com.company.mortgage.controller;

import com.company.mortgage.repository.model.InterestRateEntity;
import com.company.mortgage.response.InterestRateResponse;
import com.company.mortgage.service.InterestRateService;
import com.company.mortgage.service.mapper.InterestRateMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = InterestRateController.class)
class InterestRateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InterestRateService interestRateService;

    @MockitoBean
    private InterestRateMapper interestRateMapper;

    @Test
    void shouldReturnInterestRates() throws Exception {
        List<InterestRateEntity> rates = List.of(
                InterestRateEntity.builder()
                        .maturityPeriod(10)
                        .interestRate(BigDecimal.valueOf(3.5))
                        .lastUpdatedAt(LocalDateTime.now())
                        .build()
        );

        InterestRateResponse response =
                new InterestRateResponse(
                        10,
                        BigDecimal.valueOf(3.5),
                        LocalDateTime.now()
                );

        when(interestRateService.getAllRates()).thenReturn(rates);
        when(interestRateMapper.toMortgageRateResponseList(interestRateService.getAllRates()))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/v1/api/interest-rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].maturityPeriod").value(10))
                .andExpect(jsonPath("$[0].interestRate").value(3.5));
    }
}
