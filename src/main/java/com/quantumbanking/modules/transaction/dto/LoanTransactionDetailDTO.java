package com.quantumbanking.modules.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quantumbanking.modules.loan.domain.LoanStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record LoanTransactionDetailDTO(UUID id,
                                       @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
                                       Instant createdAt,
                                       BigDecimal amount,
                                       String description,
                                       BigDecimal totalAmount,
                                       BigDecimal installmentAmount,
                                       Integer installments,
                                       Integer paidInstallments,
                                       BigDecimal interestRate,
                                       LocalDate startDate,
                                       LocalDate endDate,
                                       LoanStatus status) implements TransactionDetailResponse {
}
