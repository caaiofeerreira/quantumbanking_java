package com.quantumbanking.modules.client.dto;

import com.quantumbanking.modules.account.domain.AccountType;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.shared.dto.AddressRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClientRegistrationDTO(@NotBlank(message = "O nome é obrigatório.")
                                    String name,

                                    @NotBlank(message = "O CPF é obrigatório.")
                                    String cpf,

                                    @NotBlank(message = "O telefone é obrigatório.")
                                    String phone,

                                    @Email(message = "O e-mail informado é inválido.")
                                    String email,

                                    @NotBlank(message = "A senha é obrigatória.")
                                    String password,

                                    @NotNull(message = "Os dados do endereço são obrigatórios.")
                                    @Valid AddressRequestDTO address,

                                    @NotNull(message = "O tipo de cliente deve ser informado.")
                                    ClientType clientType,

                                    @NotNull(message = "O tipo de conta deve ser informado.")
                                    AccountType accountType,

                                    @NotBlank(message = "O número da agência é obrigatório.")
                                    String agencyNumber,

                                    @Valid CompanyRegistrationDTO company) {
}