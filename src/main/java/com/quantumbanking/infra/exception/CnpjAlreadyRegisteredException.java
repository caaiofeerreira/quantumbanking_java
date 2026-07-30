package com.quantumbanking.infra.exception;

public class CnpjAlreadyRegisteredException extends RuntimeException {
    public CnpjAlreadyRegisteredException(String message) {
        super(message);
    }
}