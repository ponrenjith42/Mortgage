package com.company.mortgage.exception;

import lombok.Getter;

@Getter
public class InterestRateNotFoundException extends RuntimeException {

    private final int maturityPeriod;
    private static final int NO_MATURITY_PERIOD=-1;

    public InterestRateNotFoundException(int maturityPeriod) {
        super("No interest rate found for maturity period: " + maturityPeriod);
        this.maturityPeriod = maturityPeriod;
    }

    public InterestRateNotFoundException(String message) {
        super(message);
        this.maturityPeriod = NO_MATURITY_PERIOD;
    }
}
