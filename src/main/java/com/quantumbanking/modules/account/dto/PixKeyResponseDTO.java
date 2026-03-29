package com.quantumbanking.modules.account.dto;

import com.quantumbanking.modules.account.domain.PixKeyType;

import java.util.UUID;

public record PixKeyResponseDTO(UUID id,
                                String key,
                                PixKeyType type) {
}