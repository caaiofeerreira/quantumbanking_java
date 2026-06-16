package com.quantumbanking.modules.bank.dto;

import com.quantumbanking.modules.shared.dto.AddressRequestDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AgencyRegistrationDTO(@NotBlank String agencyName,
                                    @NotBlank String agencyNumber,
                                    @NotBlank String phone,
                                    @NotNull AddressRequestDTO address,
                                    @NotBlank String compe) {
}