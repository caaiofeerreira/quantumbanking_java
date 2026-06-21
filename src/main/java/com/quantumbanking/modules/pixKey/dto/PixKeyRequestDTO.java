package com.quantumbanking.modules.pixKey.dto;

import jakarta.validation.constraints.NotBlank;

public record PixKeyRequestDTO(@NotBlank String key) {
}