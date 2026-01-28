package com.company.mortgage.service;

import com.company.mortgage.request.MortgageCheckRequest;
import com.company.mortgage.response.MortgageCheckResponse;

public interface MortgageCheckService {

    MortgageCheckResponse checkMortgage(MortgageCheckRequest request);
}
