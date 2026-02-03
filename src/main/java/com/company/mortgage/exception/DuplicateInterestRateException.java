package com.company.mortgage.exception;

import lombok.Getter;
import org.springframework.dao.DataIntegrityViolationException;

@Getter
public class DuplicateInterestRateException extends RuntimeException {
    private static final int NO_MATURITY_PERIOD=-1;
    private final int maturityPeriod;

    public DuplicateInterestRateException(String message, DataIntegrityViolationException e) {
        super(message,e);
        this.maturityPeriod = NO_MATURITY_PERIOD;
    }

    public DuplicateInterestRateException(String message,int maturityPeriod) {
        super(message);
        this.maturityPeriod = maturityPeriod;
    }
}