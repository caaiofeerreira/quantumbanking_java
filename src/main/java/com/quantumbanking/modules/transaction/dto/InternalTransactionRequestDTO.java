package com.quantumbanking.modules.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record InternalTransactionRequestDTO(@NotBlank String destinationAccountNumber,
                                            @NotNull BigDecimal amount,
                                            @NotBlank String agencyNumber,
                                            String description) {
}