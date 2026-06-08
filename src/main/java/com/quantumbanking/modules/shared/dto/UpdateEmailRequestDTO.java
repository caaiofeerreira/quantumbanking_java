package com.quantumbanking.modules.shared.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateEmailRequestDTO(@NotBlank(message = "Email é obrigatório.")
                                    String email) {
}
