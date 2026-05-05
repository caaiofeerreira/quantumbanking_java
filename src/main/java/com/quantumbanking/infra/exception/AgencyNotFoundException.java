package com.quantumbanking.infra.exception;

public class AgencyNotFoundException extends  RuntimeException {
    public AgencyNotFoundException(String message) {
        super(message);
    }
}
