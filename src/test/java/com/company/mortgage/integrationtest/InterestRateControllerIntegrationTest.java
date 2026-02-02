package com.company.mortgage.integrationtest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InterestRateControllerIntegrationTest {

    private static final String TRACE_ID = "d3b07384-d9a3-4f76-9e0a-2f3b6c4e1a7b";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetInterestRates() throws Exception {
        mockMvc.perform(get("/v1/api/interest-rates")
                .header("X-Trace-Id", TRACE_ID))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Trace-Id", TRACE_ID))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4))

                // Check each maturity period exists
                .andExpect(jsonPath("$[?(@.maturityPeriod == 5)]").exists())
                .andExpect(jsonPath("$[?(@.maturityPeriod == 10)]").exists())
                .andExpect(jsonPath("$[?(@.maturityPeriod == 15)]").exists())
                .andExpect(jsonPath("$[?(@.maturityPeriod == 20)]").exists())

                // Check interest rate values
                .andExpect(jsonPath("$[?(@.maturityPeriod == 5)].interestRate").value(2.0))
                .andExpect(jsonPath("$[?(@.maturityPeriod == 10)].interestRate").value(3.0))
                .andExpect(jsonPath("$[?(@.maturityPeriod == 15)].interestRate").value(3.5))
                .andExpect(jsonPath("$[?(@.maturityPeriod == 20)].interestRate").value(4.0))

                // check lastUpdate exists for each
                .andExpect(jsonPath("$[*].lastUpdate").isNotEmpty());
    }

}
