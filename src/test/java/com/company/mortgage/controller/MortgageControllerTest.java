package com.company.mortgage.controller;

import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.response.MortgageCheckResponse;
import com.company.mortgage.service.MortgageCheckService;
import com.company.mortgage.util.TestData;
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

    // ------------------- Valid request -------------------
    @Test
    void shouldReturnMortgageCheckResult() throws Exception {
        MortgageCheckRequest request = TestData.getMortgageCheckRequest("50000",10,"200000","250000");

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

    // ------------------- Validation tests -------------------
    @Test
    void missingIncome_ReturnsBadRequest() throws Exception {
        MortgageCheckRequest request = TestData.getMortgageCheckRequest(null, 10, "200000", "250000");

        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("income: Income is required"))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void negativeIncome_ReturnsBadRequest() throws Exception {
        MortgageCheckRequest request = TestData.getMortgageCheckRequest("-50000", 10, "200000", "250000");

        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("income: Income must be greater than 0"))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void invalidMaturity_ReturnsBadRequest() throws Exception {
        MortgageCheckRequest request = TestData.getMortgageCheckRequest("50000", 0, "200000", "250000");

        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("maturityPeriod: Maturity period must be at least 1 year"))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void missingLoanValue_ReturnsBadRequest() throws Exception {
        MortgageCheckRequest request = TestData.getMortgageCheckRequest("50000", 10, null, "250000");

        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("loanValue: Loan value is required"))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void negativeLoanValue_ReturnsBadRequest() throws Exception {
        MortgageCheckRequest request = TestData.getMortgageCheckRequest("50000", 10, "-200000", "250000");

        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("loanValue: Loan value must be greater than 0"))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void missingHomeValue_ReturnsBadRequest() throws Exception {
        MortgageCheckRequest request = TestData.getMortgageCheckRequest("50000", 10, "200000", null);

        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("homeValue: Home value is required"))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void negativeHomeValue_ReturnsBadRequest() throws Exception {
        MortgageCheckRequest request = TestData.getMortgageCheckRequest("50000", 10, "200000", "-250000");

        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("homeValue: Home value must be greater than 0"))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldReturn405ForInvalidHttpMethod() throws Exception {
        mockMvc.perform(get("/v1/api/mortgage-check"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void shouldReturn415ForUnsupportedContentType() throws Exception {
        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_XML)
                        .content("<xml></xml>"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldReturn406ForUnsupportedAcceptHeader() throws Exception {
        mockMvc.perform(post("/v1/api/mortgage-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_XML)
                        .content("{}"))
                .andExpect(status().isNotAcceptable());
    }
}