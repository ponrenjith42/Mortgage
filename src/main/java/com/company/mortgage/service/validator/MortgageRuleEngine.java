package com.company.mortgage.service.validator;

import com.company.mortgage.request.MortgageCheckRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MortgageRuleEngine {

    private final List<MortgageRuleValidator> validators;

    public void validate(MortgageCheckRequest request) {
        validators.forEach(v -> v.validate(request));
    }
}