package com.quantumbanking.infra.exception;

public class IncompatibleAccountTypeException extends RuntimeException {
    public IncompatibleAccountTypeException(String message) {
        super(message);
    }
}
