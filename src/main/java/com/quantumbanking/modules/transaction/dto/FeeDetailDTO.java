package com.quantumbanking.modules.transaction.dto;

import java.math.BigDecimal;

public record FeeDetailDTO(boolean charged,
                           BigDecimal amount,
                           String reason) {
}