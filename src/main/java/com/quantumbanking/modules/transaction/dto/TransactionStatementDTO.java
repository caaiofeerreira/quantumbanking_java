package com.quantumbanking.modules.transaction.dto;

import com.quantumbanking.modules.transaction.domain.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionStatementDTO(UUID id,
                                      TransactionType type,
                                      BigDecimal amount,
                                      String description,
                                      String counterpartName,
                                      LocalDateTime createdAT) {
}