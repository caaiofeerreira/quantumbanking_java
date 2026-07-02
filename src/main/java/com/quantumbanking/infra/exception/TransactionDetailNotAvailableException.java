package com.quantumbanking.infra.exception;

public class TransactionDetailNotAvailableException extends RuntimeException {
    public TransactionDetailNotAvailableException(String message) {
        super(message);
    }
}