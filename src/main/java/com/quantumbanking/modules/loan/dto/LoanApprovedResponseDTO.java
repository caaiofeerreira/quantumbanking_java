package com.quantumbanking.modules.loan.dto;

import com.quantumbanking.modules.loan.domain.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record LoanApprovedResponseDTO(UUID id,
                                      LocalDateTime createdAt,
                                      BigDecimal amount,
                                      BigDecimal interestRate,
                                      Integer installments,
                                      BigDecimal totalAmount,
                                      BigDecimal installmentAmount,
                                      Integer paidInstallments,
                                      LocalDate startDate,
                                      LocalDate endDate,
                                      LoanStatus status,
                                      String managerName,
                                      String description) {

}