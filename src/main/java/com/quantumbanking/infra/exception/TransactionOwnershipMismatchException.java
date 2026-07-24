package com.quantumbanking.infra.exception;

public class TransactionOwnershipMismatchException extends RuntimeException {
    public TransactionOwnershipMismatchException(String message) {
        super(message);
    }
}