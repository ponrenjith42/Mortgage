package com.company.mortgage;

import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.service.MortgageRateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class MortgageControllerIntegrationH2Test {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MortgageRateService mortgageRateService;

    @Test
    void testGetInterestRates() throws Exception {
        mockMvc.perform(get("/v1/api/interest-rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @ParameterizedTest(name = "income={0}, maturity={1}, loan={2}, home={3} → feasible={4}")
    @CsvSource({
            "50000, 10, 150000, 200000, true",
            "30000, 10, 150000, 200000, false",
            "100000, 10, 250000, 200000, false"
    })
    void testMortgageCheckParameterized(
            BigDecimal income,
            int maturityPeriod,
            BigDecimal loanValue,
            BigDecimal homeValue,
            boolean feasible
    ) throws Exception {

        var request = new MortgageCheckRequest(income, maturityPeriod, loanValue, homeValue);

        var resultActions = mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feasible").value(feasible));

        if (feasible) {
            resultActions.andExpect(jsonPath("$.monthlyCost").isNumber());
        } else {
            resultActions.andExpect(jsonPath("$.monthlyCost").value(0));
        }
    }

    @Test
    void testMortgageCheckValidationError() throws Exception {
        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    void testMortgageCheckRateNotFound() throws Exception {
        var request = new MortgageCheckRequest(
                new BigDecimal("50000"),
                99,
                new BigDecimal("150000"),
                new BigDecimal("200000")
        );

        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("INTEREST_RATE_NOT_FOUND"))
                .andExpect(jsonPath("$.messages").isArray());
    }
}
