package com.quantumbanking.infra.exception;

public class LoanStatusException extends RuntimeException {
    public LoanStatusException(String message) {
        super(message);
    }
}