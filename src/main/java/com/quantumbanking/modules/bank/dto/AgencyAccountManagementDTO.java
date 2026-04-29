package com.quantumbanking.modules.bank.dto;

import com.quantumbanking.modules.account.domain.AccountStatus;
import com.quantumbanking.modules.account.domain.AccountType;
import com.quantumbanking.modules.client.domain.ClientType;

import java.math.BigDecimal;

public record AgencyAccountManagementDTO(String name,
                                         String cpf,
                                         String email,
                                         String phone,
                                         ClientType type,
                                         Long accountId,
                                         String accountNumber,
                                         AccountType accountType,
                                         BigDecimal balance,
                                         AccountStatus accountStatus,
                                         String agencyNumber) {
}