package com.quantumbanking.modules.client.dto;

import com.quantumbanking.modules.account.domain.AccountType;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.shared.dto.AddressRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClientRegistrationDTO(@NotBlank String name,
                                    @NotBlank String cpf,
                                    @NotBlank String phone,
                                    @Email String email,
                                    @NotBlank String password,
                                    @NotNull AddressRequestDTO address,
                                    @NotNull ClientType clientType,
                                    @NotNull AccountType accountType,
                                    @NotBlank String agencyNumber,
                                    @Valid CompanyRegistrationDTO company) {
}