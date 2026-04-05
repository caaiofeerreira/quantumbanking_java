package com.quantumbanking.modules.account.service;

import com.quantumbanking.infra.exception.AccountNotFoundException;
import com.quantumbanking.infra.exception.TransactionNotAuthorizedException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.AccountStatus;
import com.quantumbanking.modules.account.dto.StatementResponseDTO;
import com.quantumbanking.modules.account.repository.AccountRepository;
import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.dto.TransactionStatementDTO;
import com.quantumbanking.modules.transaction.mapper.TransactionMapper;
import com.quantumbanking.modules.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    public BigDecimal getBalance(User user) {

        return accountRepository.findBalanceByClientId(user.getId())
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada."));
    }

    public StatementResponseDTO getStatement(User user, Integer month, Integer year) {

        Account account = accountRepository.findByClientId(user.getId())
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada."));

        if (account.getStatus() != AccountStatus.ATIVA) {
            throw new TransactionNotAuthorizedException("Conta não está ativa.");
        }

        List<Transaction> transactions = transactionRepository.findByAccountAndPeriod(account.getId(), month, year);

        List<TransactionStatementDTO> statementDTOS = transactions
                .stream()
                .map(t -> transactionMapper.toStatementResponse(t, account))
                .toList();

        return new StatementResponseDTO(
                month,
                year,
                account.getBalance(),
                statementDTOS
        );
    }
}
