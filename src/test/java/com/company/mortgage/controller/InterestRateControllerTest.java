package com.company.mortgage.controller;

import com.company.mortgage.response.InterestRateResponse;
import com.company.mortgage.service.InterestRateService;
import com.company.mortgage.exception.InterestRateNotFoundException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = InterestRateController.class)
class InterestRateControllerTest {

    private static final String TRACE_ID = "d3b07384-d9a3-4f76-9e0a-2f3b6c4e1a7b";

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
        InterestRateResponse response =
                new InterestRateResponse(
                        10,
                        BigDecimal.valueOf(3.5),
                        LocalDateTime.of(2026, 01, 01, 11, 11, 11)
                );

        when(interestRateService.getAllRates())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/v1/api/interest-rates")
                        .header("X-Trace-Id", TRACE_ID))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Trace-Id", TRACE_ID))
                .andExpect(jsonPath("$[0].maturityPeriod").value(10))
                .andExpect(jsonPath("$[0].interestRate").value(3.5))
                .andExpect(jsonPath("$[0].lastUpdate").value("2026-01-01T11:11:11"));
    }

    @Test
    void shouldReturn404WhenNoInterestRatesFound() throws Exception {

        when(interestRateService.getAllRates())
                .thenThrow(new InterestRateNotFoundException("No interest rates found"));

        mockMvc.perform(get("/v1/api/interest-rates")
                        .header("X-Trace-Id", TRACE_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("INTEREST_RATE_NOT_FOUND"))
                .andExpect(jsonPath("$.messages[0]")
                        .value("No interest rates found"))
                .andExpect(jsonPath("$.traceId").value(TRACE_ID))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturn500ForUnexpectedException() throws Exception {

        when(interestRateService.getAllRates())
                .thenThrow(new RuntimeException("DB is down"));

        mockMvc.perform(get("/v1/api/interest-rates")
                        .header("X-Trace-Id", TRACE_ID))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code")
                        .value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.messages[0]")
                        .value("An unexpected error occurred. Please contact support."))
                .andExpect(jsonPath("$.traceId").value(TRACE_ID))
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    void shouldGenerateTraceIdWhenMissing() throws Exception {
        InterestRateResponse response =
                new InterestRateResponse(
                        5,
                        BigDecimal.valueOf(2.0),
                        LocalDateTime.now()
                );

        when(interestRateService.getAllRates())
                .thenReturn(List.of(response));

        mockMvc.perform(get("/v1/api/interest-rates"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Trace-Id"));
    }

    @Test
    void shouldReturn405ForInvalidHttpMethod() throws Exception {
        mockMvc.perform(post("/v1/api/interest-rates"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void shouldReturn406ForUnsupportedMediaType() throws Exception {
        mockMvc.perform(get("/v1/api/interest-rates")
                        .accept("application/xml"))
                .andExpect(status().isNotAcceptable());
    }
}

