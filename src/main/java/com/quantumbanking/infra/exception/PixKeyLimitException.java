package com.quantumbanking.infra.exception;

public class PixKeyLimitException extends RuntimeException {
    public PixKeyLimitException(String message) {
        super(message);
    }
}
