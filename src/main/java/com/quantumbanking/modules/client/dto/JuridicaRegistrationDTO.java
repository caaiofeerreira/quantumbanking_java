package com.quantumbanking.modules.client.dto;

import com.quantumbanking.modules.account.domain.AccountType;
import jakarta.validation.Valid;

public record JuridicaRegistrationDTO(ClientCoreDataDTO data,
                                      @Valid CompanyRegistrationDTO company,
                                      AccountType accountType,
                                      String agencyNumber) {
}