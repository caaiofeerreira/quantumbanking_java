package com.quantumbanking.infra.exception;

public class InvalidTransactionValueException extends RuntimeException {
    public InvalidTransactionValueException(String message) {
        super(message);
    }
}
