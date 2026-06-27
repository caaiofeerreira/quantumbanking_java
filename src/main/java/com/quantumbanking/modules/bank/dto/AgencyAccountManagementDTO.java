package com.quantumbanking.modules.bank.dto;

import com.quantumbanking.modules.account.dto.AccountSummaryDTO;
import com.quantumbanking.modules.client.domain.ClientType;

public record AgencyAccountManagementDTO(String name,
                                         String email,
                                         String phone,
                                         ClientType type,
                                         AccountSummaryDTO account) {
}