package com.quantumbanking.modules.manager.dto;

import com.quantumbanking.modules.account.domain.AccountStatus;
import com.quantumbanking.modules.account.domain.AccountType;
import com.quantumbanking.modules.account.dto.StatementSummaryDTO;
import com.quantumbanking.modules.client.dto.ClientSummaryDTO;
import com.quantumbanking.modules.transaction.dto.TransactionStatementDTO;

import java.math.BigDecimal;
import java.util.List;

public record ManagerStatementResponseDTO(ClientSummaryDTO client,
                                          String accountNumber,
                                          AccountType accountType,
                                          AccountStatus accountStatus,
                                          BigDecimal currentBalance,
                                          StatementSummaryDTO summary,
                                          List<TransactionStatementDTO> transactions) {
}