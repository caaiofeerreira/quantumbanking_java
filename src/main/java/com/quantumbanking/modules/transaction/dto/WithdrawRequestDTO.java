package com.quantumbanking.modules.transaction.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WithdrawRequestDTO(@NotNull BigDecimal amount,
                                 String description) {
}