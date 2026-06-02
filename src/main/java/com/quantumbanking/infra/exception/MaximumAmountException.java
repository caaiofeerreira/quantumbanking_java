package com.quantumbanking.infra.exception;

public class MaximumAmountException extends RuntimeException {
    public MaximumAmountException(String message) {
        super(message);
    }
}