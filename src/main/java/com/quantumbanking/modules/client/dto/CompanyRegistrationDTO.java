package com.quantumbanking.modules.client.dto;

import com.quantumbanking.modules.shared.domain.address.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompanyRegistrationDTO(@NotBlank String companyName,
                                     @NotBlank String tradeName,
                                     @NotBlank String cnpj,
                                     @NotBlank String stateRegistration,
                                     @NotNull @Valid Address address) {
}