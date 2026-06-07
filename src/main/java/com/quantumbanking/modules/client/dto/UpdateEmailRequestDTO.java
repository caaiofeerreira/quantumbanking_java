package com.quantumbanking.modules.client.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateEmailRequestDTO(@NotBlank(message = "Email é obrigatório.")
                                    String email) {
}
