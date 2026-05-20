package com.quantumbanking.modules.loan.dto;

import com.quantumbanking.modules.loan.domain.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LoanApprovedResponseDTO(UUID id,
                                      BigDecimal amount,
                                      BigDecimal interestRate,
                                      Integer installments,
                                      BigDecimal totalAmount,
                                      BigDecimal installmentAmount,
                                      LocalDate startDate,
                                      LocalDate endDate,
                                      LoanStatus status,
                                      String managerName) {

}