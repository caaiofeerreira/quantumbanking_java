package com.quantumbanking.infra.exception;

public class DuplicateAccountTypeException extends RuntimeException {
    public DuplicateAccountTypeException(String message) {
        super(message);
    }
}