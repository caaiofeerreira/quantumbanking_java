package com.quantumbanking.modules.account.dto;

import java.math.BigDecimal;
import java.util.List;

public record MultiMonthStatementResponseDTO(BigDecimal currentBalance,
                                             List<MonthlyStatementDTO> months) {
}