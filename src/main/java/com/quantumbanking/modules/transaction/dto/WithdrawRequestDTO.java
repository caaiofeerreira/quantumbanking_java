package com.quantumbanking.modules.transaction.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WithdrawRequestDTO(@NotNull @Positive
                                 BigDecimal amount,
                                 String description) {
}