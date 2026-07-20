package com.quantumbanking.infra.event;

import java.util.Set;

public record AccountBalanceChangedEvent(Set<String> accountNumbers) {
    public AccountBalanceChangedEvent(String accountNumber) {
        this(Set.of(accountNumber));
    }
}