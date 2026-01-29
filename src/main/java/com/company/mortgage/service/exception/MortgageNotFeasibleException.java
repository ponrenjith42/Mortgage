package com.company.mortgage.service.exception;

public class MortgageNotFeasibleException extends RuntimeException {

    public MortgageNotFeasibleException(String message) {
        super(message);
    }
}