package com.quantumbanking.modules.transaction.dto;

import com.quantumbanking.modules.transaction.domain.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record InternalTransactionResponseDTO(UUID id,
                                             String accountDestiny,
                                             BigDecimal amount,
                                             TransactionType transactionType,
                                             String agencyNumber) {
}