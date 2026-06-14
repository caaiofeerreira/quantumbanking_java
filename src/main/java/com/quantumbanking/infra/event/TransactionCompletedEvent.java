package com.quantumbanking.infra.event;

import java.util.Set;

public record TransactionCompletedEvent(Set<String> accountNumbers) {
    public TransactionCompletedEvent(String accountNumber) {
        this(Set.of(accountNumber));
    }
}