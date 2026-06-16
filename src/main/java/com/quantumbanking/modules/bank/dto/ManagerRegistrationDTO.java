package com.quantumbanking.modules.bank.dto;

import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.dto.AddressRequestDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ManagerRegistrationDTO(@NotBlank String name,
                                     @NotBlank String cpf,
                                     @NotBlank String phone,
                                     @Email String email,
                                     @NotBlank String password,
                                     @NotNull AddressRequestDTO address,
                                     @NotBlank String agencyNumber) {

}