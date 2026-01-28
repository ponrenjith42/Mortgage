package com.company.mortgage.service.exception;

public class DuplicateInterestRateException extends RuntimeException {

    private final int maturityPeriod;

    public DuplicateInterestRateException(int maturityPeriod) {
        super("Interest rate already exists for maturity: " + maturityPeriod);
        this.maturityPeriod = maturityPeriod;
    }

    public int getMaturityPeriod() {
        return maturityPeriod;
    }
}