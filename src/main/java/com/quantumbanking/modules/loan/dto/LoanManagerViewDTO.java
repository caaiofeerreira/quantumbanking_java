package com.quantumbanking.modules.loan.dto;

import com.quantumbanking.modules.account.domain.AccountType;
import com.quantumbanking.modules.loan.domain.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record LoanManagerViewDTO(String clientName,
                                 String accountNumber,
                                 BigDecimal balance,
                                 AccountType accountType,

                                 UUID loanId,
                                 BigDecimal amount,
                                 BigDecimal interestRate,
                                 Integer installments,
                                 BigDecimal installmentAmount,
                                 BigDecimal totalAmount,
                                 String description,
                                 LocalDateTime createdAt,
                                 LoanStatus status) {}
