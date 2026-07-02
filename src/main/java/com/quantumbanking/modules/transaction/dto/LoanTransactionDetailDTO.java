package com.quantumbanking.modules.transaction.dto;

import com.quantumbanking.modules.loan.domain.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record LoanTransactionDetailDTO(UUID id,
                                       LocalDateTime createdAt,
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
