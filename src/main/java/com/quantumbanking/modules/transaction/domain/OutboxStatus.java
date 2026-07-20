package com.quantumbanking.modules.transaction.domain;

public enum OutboxStatus {
    PENDING_PUBLISH,
    PUBLISHED,
    FAILED
}