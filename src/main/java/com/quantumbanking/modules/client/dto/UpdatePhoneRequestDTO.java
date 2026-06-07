package com.quantumbanking.modules.client.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePhoneRequestDTO(@NotBlank(message = "Telefone é obrigatório.")
                                    String phone) {
}