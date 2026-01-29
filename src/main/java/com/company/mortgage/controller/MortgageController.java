package com.company.mortgage.controller;

import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.response.MortgageCheckResponse;
import com.company.mortgage.service.MortgageCheckService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api")
@Tag(name = "Mortgage Check", description = "Endpoints to calculate mortgage feasibility and monthly costs")
public class MortgageController {

    private final MortgageCheckService mortgageCheckService;

    @PostMapping(
            value = "/mortgage-check",
            consumes = "application/json",
            produces = "application/json"
    )
    public MortgageCheckResponse checkMortgage(@Valid @RequestBody MortgageCheckRequest request) {
        return mortgageCheckService.checkMortgage(request);
    }
}
