package com.quantumbanking.modules.account.dto;

import com.quantumbanking.modules.account.domain.PixKeyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PixKeyRequestDTO(@NotBlank String key,
                               @NotNull PixKeyType type) {
}