package com.quantumbanking.infra.exception;

public class InvalidClientTypeException extends RuntimeException {
    public InvalidClientTypeException(String message) {
        super(message);
    }
}
