package com.company.mortgage.controller;

import com.company.mortgage.response.InterestRateResponse;
import com.company.mortgage.service.InterestRateService;
import com.company.mortgage.service.mapper.InterestRateMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api")
@Tag(name = "Interest Rates", description = "Endpoints to view or manage mortgage interest rates")
public class InterestRateController {

    private final InterestRateMapper interestRateMapper;
    private final InterestRateService interestRateService;

    @GetMapping(
            value = "/interest-rates",
            produces = "application/json"
    )
    public List<InterestRateResponse> getAllRates() {
        return interestRateMapper.toMortgageRateResponseList(interestRateService.getAllRates());
    }

}
