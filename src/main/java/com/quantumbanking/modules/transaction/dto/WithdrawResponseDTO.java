package com.quantumbanking.modules.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quantumbanking.modules.transaction.domain.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record WithdrawResponseDTO(UUID id,
                                  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
                                  Instant createdAt,
                                  TransactionType type,
                                  BigDecimal amount,
                                  FeeDetailDTO fee) implements TransactionDetailResponse {
}