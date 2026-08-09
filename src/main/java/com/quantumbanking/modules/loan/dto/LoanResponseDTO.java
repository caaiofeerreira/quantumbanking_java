package com.quantumbanking.modules.loan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quantumbanking.modules.loan.domain.LoanStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LoanResponseDTO(UUID id,
                              BigDecimal amount,
                              BigDecimal totalAmount,
                              BigDecimal installmentAmount,
                              BigDecimal interestRate,
                              Integer installments,
                              String description,
                              LoanStatus status,
                              @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
                              Instant createdAt) {
}