package com.quantumbanking.infra.exception;

public class AgencyAlreadyExistsException extends RuntimeException {
    public AgencyAlreadyExistsException(String message) {
        super(message);
    }
}
