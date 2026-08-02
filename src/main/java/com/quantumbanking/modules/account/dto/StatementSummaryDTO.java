package com.quantumbanking.modules.account.dto;

import java.math.BigDecimal;

public record StatementSummaryDTO(BigDecimal totalIn, BigDecimal totalOut) {
}
