package com.quantumbanking.infra.event;

import java.util.Set;

public record TransactionCompletedEvent(Set<Long> userIds) {
    public TransactionCompletedEvent(Long userId) {
        this(Set.of(userId));
    }
}