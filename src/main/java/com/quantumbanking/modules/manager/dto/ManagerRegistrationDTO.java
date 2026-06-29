package com.quantumbanking.modules.manager.dto;

import com.quantumbanking.modules.shared.dto.AddressRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ManagerRegistrationDTO(@NotBlank(message = "O nome é obrigatório.")
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

                                     @NotBlank(message = "O número da agência é obrigatório.")
                                     String agencyNumber) {
}