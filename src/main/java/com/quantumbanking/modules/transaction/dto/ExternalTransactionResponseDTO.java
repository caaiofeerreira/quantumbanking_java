package com.quantumbanking.modules.transaction.dto;

import com.quantumbanking.modules.transaction.domain.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ExternalTransactionResponseDTO(UUID id,
                                             String name,
                                             String destinyAccount,
                                             String destinyAgency,
                                             String bankCode,
                                             String destinyDocument,
                                             BigDecimal amount,
                                             String description,
                                             TransactionType type,
                                             LocalDateTime createdAt) {
}