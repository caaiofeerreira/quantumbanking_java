package com.quantumbanking.infra.exception;

public class TransactionNotAuthorizedException extends RuntimeException {

    public TransactionNotAuthorizedException(String message) {
        super(message);
    }
}