package com.quantumbanking.modules.loan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quantumbanking.modules.loan.domain.LoanStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record LoanApprovedResponseDTO(UUID id,
                                      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Sao_Paulo")
                                      Instant createdAt,
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