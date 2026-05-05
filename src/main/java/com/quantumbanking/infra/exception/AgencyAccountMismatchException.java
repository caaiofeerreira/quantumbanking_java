package com.quantumbanking.infra.exception;

public class AgencyAccountMismatchException extends RuntimeException {
    public AgencyAccountMismatchException(String message) {
        super(message);
    }
}
