package com.quantumbanking.modules.account.dto;

import jakarta.validation.constraints.NotBlank;

public record PixKeyRequestDTO(@NotBlank String key) {
}