package com.quantumbanking.modules.loan.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record LoanRequestDTO(@NotNull @Positive BigDecimal amount,
                             @NotNull @Positive Integer installments,
                             String description) {
}