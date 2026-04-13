package com.quantumbanking.modules.bank.dto;

import com.quantumbanking.modules.shared.domain.address.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AgencyRegistrationDTO(@NotBlank String agencyName,
                                    @NotBlank String agencyNumber,
                                    @NotBlank String phone,
                                    @NotNull @Valid Address address,
                                    @NotBlank String bankCode) {
}