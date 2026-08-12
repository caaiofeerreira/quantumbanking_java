package com.quantumbanking.modules.pixKey.dto;

import com.quantumbanking.modules.pixKey.domain.PixKeyType;

public record PixKeyDetectionResult(PixKeyType type, String normalizedKey) {
}