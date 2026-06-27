package com.quantumbanking.modules.account.dto;

import com.quantumbanking.modules.account.domain.AccountStatus;
import com.quantumbanking.modules.account.domain.AccountType;

import java.math.BigDecimal;

public record AccountResponseDTO(String accountNumber,
                                 AccountType accountType,
                                 BigDecimal balance,
                                 AccountStatus status,
                                 String agencyNumber) {

}