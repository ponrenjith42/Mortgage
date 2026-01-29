package com.company.mortgage.controller;

import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.response.MortgageCheckResponse;
import com.company.mortgage.service.MortgageCheckService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MortgageController.class)
class MortgageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MortgageCheckService mortgageCheckService;

    @Test
    void shouldReturnMortgageCheckResult() throws Exception {
        MortgageCheckRequest request = new MortgageCheckRequest(
                BigDecimal.valueOf(5000),
                20,
                BigDecimal.valueOf(200000),
                BigDecimal.valueOf(250000)
        );

        MortgageCheckResponse response =
                new MortgageCheckResponse(true, BigDecimal.valueOf(1200.50));

        when(mortgageCheckService.checkMortgage(request)).thenReturn(response);

        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feasible").value(true))
                .andExpect(jsonPath("$.monthlyCost").value(1200.50));
    }
}