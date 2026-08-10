package com.quantumbanking.modules.account.dto;

import com.quantumbanking.modules.transaction.dto.TransactionStatementDTO;

import java.util.List;

public record MonthlyStatementDTO(Integer month,
                                  Integer year,
                                  StatementSummaryDTO summary,
                                  List<TransactionStatementDTO> transactions) {
}