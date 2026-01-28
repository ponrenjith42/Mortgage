package com.company.mortgage.service.exception;


public class InterestRateNotFoundException extends RuntimeException {

    public InterestRateNotFoundException(int maturityPeriod) {
        super("No interest rate found for maturity period: " + maturityPeriod);
    }

    public InterestRateNotFoundException(String message) {
        super(message);
    }
}
