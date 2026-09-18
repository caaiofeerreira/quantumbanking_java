package com.quantumbanking.modules.client.dto;

import com.quantumbanking.modules.account.domain.AccountType;

public record FisicaRegistrationDTO(ClientCoreDataDTO data,
                                    AccountType accountType,
                                    String agencyNumber) {
}