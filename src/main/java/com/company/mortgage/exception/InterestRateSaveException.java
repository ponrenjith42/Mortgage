package com.company.mortgage.exception;

public class InterestRateSaveException extends RuntimeException {
    public InterestRateSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}