package com.quantumbanking.infra.exception;

public class PixKeyNotFoundException extends RuntimeException {
    public PixKeyNotFoundException(String message) {
        super(message);
    }
}