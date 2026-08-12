package com.quantumbanking.modules.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record PixTransactionRequestDTO(@NotBlank String key,
                                       @NotNull BigDecimal amount,
                                       String description) {

}