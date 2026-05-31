package com.quantumbanking.modules.account.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@RequiredArgsConstructor
public enum AccountType {
    POUPANCA(4, new BigDecimal("6.50")),
    CORRENTE(10, new BigDecimal("4.50")),
    JURIDICA(20, new BigDecimal("8.00"));

    private final int freeWithdrawals;
    private final BigDecimal feeAmount;
}