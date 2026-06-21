package com.quantumbanking.modules.pixKey.dto;

import com.quantumbanking.modules.pixKey.domain.PixKeyType;

public record PixKeyResponseDTO(String key,
                                PixKeyType type) {
}