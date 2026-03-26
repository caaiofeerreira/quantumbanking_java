package com.quantumbanking.modules.transaction.dto;

import com.quantumbanking.modules.transaction.domain.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DepositResponseDTO(UUID id,
                                 BigDecimal amount,
                                 TransactionType type,
                                 String description,
                                 LocalDateTime createdAt) {
}