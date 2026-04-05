package com.quantumbanking.modules.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ExternalTransactionRequestDTO(
        @NotBlank String destinyName,
        @NotBlank String destinyAccount,
        @NotBlank String destinyAgency,
        @NotBlank String bankCode,
        @NotBlank String destinyDocument,
        @NotNull @Positive BigDecimal amount,
        String description) {

}