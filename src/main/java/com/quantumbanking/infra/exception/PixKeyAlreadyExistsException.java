package com.quantumbanking.infra.exception;

public class PixKeyAlreadyExistsException extends RuntimeException {
    public PixKeyAlreadyExistsException(String message) {
        super(message);
    }
}
