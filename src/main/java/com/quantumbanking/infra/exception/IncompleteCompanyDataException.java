package com.quantumbanking.infra.exception;

public class IncompleteCompanyDataException extends RuntimeException {
    public IncompleteCompanyDataException(String message) {
        super(message);
    }
}
