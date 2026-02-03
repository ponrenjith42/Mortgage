package com.company.mortgage.exception;

// Define a custom exception
public class InterestRateSaveException extends RuntimeException {
    public InterestRateSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}