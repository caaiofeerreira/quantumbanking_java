package com.quantumbanking.infra.exception;

public class SamePhoneException extends RuntimeException {
    public SamePhoneException(String message) {
        super(message);
    }
}