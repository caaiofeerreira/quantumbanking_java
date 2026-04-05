package com.quantumbanking.modules.account.dto;

import com.quantumbanking.modules.transaction.dto.TransactionStatementDTO;

import java.math.BigDecimal;
import java.util.List;

public record StatementResponseDTO(Integer month,
                                   Integer year,
                                   BigDecimal currentBalance,
                                   List<TransactionStatementDTO> transactions) {
}
