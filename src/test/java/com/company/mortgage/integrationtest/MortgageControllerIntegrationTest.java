package com.company.mortgage.integrationtest;

import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.util.TestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static com.company.mortgage.util.TestData.getMortgageCheckRequest;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class  MortgageControllerIntegrationTest {

    private static final String TRACE_ID = "d3b07384-d9a3-4f76-9e0a-2f3b6c4e1a7b";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @ParameterizedTest(name = "income={0}, maturity={1}, loan={2}, home={3} → feasible={4}")
    @CsvSource({
            // feasible
            "50000, 10, 150000, 200000, true, 1448.41, ''",
            // infeasible - low income
            "30000, 10, 150000, 200000, false, 0, 'Loan value exceeds 4 times the income'",
            // infeasible - loan > home value
            "100000, 10, 250000, 200000, false, 0, 'Loan value exceeds home value'"
    })
    void testMortgageCheckParameterized(
            String income,
            int maturityPeriod,
            String loanValue,
            String homeValue,
            boolean feasible,
            BigDecimal expectedMonthlyCost,
            String expectedMessage
    ) throws Exception {

        MortgageCheckRequest request = getMortgageCheckRequest(income, maturityPeriod, loanValue, homeValue);

        var perform = mockMvc.perform(post("/v1/api/mortgage-check")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Trace-Id", TRACE_ID)
                .content(objectMapper.writeValueAsString(request)));

        if (feasible) {
            perform.andExpect(status().isOk())
                    .andExpect(header().string("X-Trace-Id", TRACE_ID))
                    .andExpect(jsonPath("$.feasible").value(true))
                    .andExpect(jsonPath("$.monthlyCost").value(expectedMonthlyCost));
        } else {
            perform.andExpect(status().isUnprocessableEntity())
                    .andExpect(header().string("X-Trace-Id", TRACE_ID))
                    .andExpect(jsonPath("$.code").value("MORTGAGE_NOT_FEASIBLE"))
                    .andExpect(jsonPath("$.messages[0]").value(expectedMessage))
                    .andExpect(jsonPath("$.status").value(422));
        }
    }

    @Test
    void testMortgageCheckValidationError() throws Exception {
        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Trace-Id", TRACE_ID)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("X-Trace-Id", TRACE_ID))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    void testMortgageCheckRateNotFound() throws Exception {
        MortgageCheckRequest request = TestData.getMortgageCheckRequest("50000",99,"200000","250000");

        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Trace-Id", TRACE_ID)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(header().string("X-Trace-Id", TRACE_ID))
                .andExpect(jsonPath("$.code").value("INTEREST_RATE_NOT_FOUND"))
                .andExpect(jsonPath("$.messages").isArray());
    }
}
