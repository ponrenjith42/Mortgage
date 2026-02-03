package com.company.mortgage.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DuplicateInterestRateException extends RuntimeException {

    public DuplicateInterestRateException(String message, DataIntegrityViolationException e) {
        super(message,e);
    }
}