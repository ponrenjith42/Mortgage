package com.company.mortgage.exception;

public class MortgageNotFeasibleException extends RuntimeException {

    public MortgageNotFeasibleException(String message) {
        super(message);
    }
}