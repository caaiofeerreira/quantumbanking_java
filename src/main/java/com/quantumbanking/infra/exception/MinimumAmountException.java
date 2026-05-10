package com.quantumbanking.infra.exception;

public class MinimumAmountException extends RuntimeException {
    public MinimumAmountException(String message) {
        super(message);
    }
}
