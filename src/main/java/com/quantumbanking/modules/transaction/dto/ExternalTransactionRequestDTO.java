package com.quantumbanking.modules.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ExternalTransactionRequestDTO(@NotBlank String destinationName,
                                            @NotBlank String destinationAccount,
                                            @NotBlank String destinationAgency,
                                            @NotBlank String compe,
                                            @NotBlank String destinationDocument,
                                            @NotNull BigDecimal amount,
                                            String description) {

}