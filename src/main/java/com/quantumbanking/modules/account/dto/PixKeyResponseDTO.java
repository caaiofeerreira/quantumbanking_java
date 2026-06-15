package com.quantumbanking.modules.account.dto;

import com.quantumbanking.modules.account.domain.PixKeyType;

import java.util.UUID;

public record PixKeyResponseDTO(String key,
                                PixKeyType type) {
}