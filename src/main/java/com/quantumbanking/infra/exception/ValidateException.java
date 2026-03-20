package com.quantumbanking.infra.exception;

public class ValidateException extends RuntimeException {

    public ValidateException(String message) {
        super(message);
    }
}