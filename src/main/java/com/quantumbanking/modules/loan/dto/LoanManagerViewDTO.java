package com.quantumbanking.modules.loan.dto;

import com.quantumbanking.modules.account.domain.AccountType;

import java.math.BigDecimal;

public record LoanManagerViewDTO(String clientName,
                                 String accountNumber,
                                 BigDecimal balance,
                                 AccountType type,
                                 LoanResponseDTO loan) {

}
