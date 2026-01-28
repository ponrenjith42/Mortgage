package com.company.mortgage.controller;

import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.response.MortgageCheckResponse;
import com.company.mortgage.response.MortgageRateResponse;
import com.company.mortgage.service.MortgageRateService;
import com.company.mortgage.service.MortgageCheckService;
import com.company.mortgage.service.mapper.MortgageRateMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api")
public class MortgageController {

    private final MortgageRateMapper mortgageRateMapper;
    private final MortgageRateService mortgageRateService;
    private final MortgageCheckService mortgageCheckService;


    @GetMapping(
            value = "/interest-rates",
            produces = "application/json"
    )
    public List<MortgageRateResponse> getAllRates() {
        return mortgageRateMapper.toMortgageRateResponseList(mortgageRateService.getAllRates());
    }

    @PostMapping(
            value = "/mortgage-check",
            consumes = "application/json",
            produces = "application/json"
    )
    public MortgageCheckResponse checkMortgage(@Valid @RequestBody MortgageCheckRequest request) {
        return mortgageCheckService.checkMortgage(request);
    }
}
