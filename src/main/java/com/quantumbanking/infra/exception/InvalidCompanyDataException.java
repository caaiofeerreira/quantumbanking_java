package com.quantumbanking.infra.exception;

public class InvalidCompanyDataException extends RuntimeException {
    public InvalidCompanyDataException(String message) {
        super(message);
    }
}
