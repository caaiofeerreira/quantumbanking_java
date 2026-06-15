package com.quantumbanking.modules.loan.dto;

import com.quantumbanking.modules.loan.domain.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record LoanResponseDTO(UUID id,
                              BigDecimal amount,
                              BigDecimal totalAmount,
                              BigDecimal installmentAmount,
                              BigDecimal interestRate,
                              Integer installments,
                              String description,
                              LoanStatus status,
                              LocalDateTime createdAt) {
}