package com.quantumbanking.modules.transaction.service;

import com.quantumbanking.infra.exception.AccountNotFoundException;
import com.quantumbanking.infra.exception.TransactionNotAuthorizedException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.AccountStatus;
import com.quantumbanking.modules.account.repository.AccountRepository;
import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.domain.TransactionType;
import com.quantumbanking.modules.transaction.dto.DepositRequestDTO;
import com.quantumbanking.modules.transaction.dto.DepositResponseDTO;
import com.quantumbanking.modules.transaction.dto.WithdrawRequestDTO;
import com.quantumbanking.modules.transaction.dto.WithdrawResponseDTO;
import com.quantumbanking.modules.transaction.mapper.TransactionMapper;
import com.quantumbanking.modules.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;


    private Account getAccountByUser(User user) {

        return accountRepository.findByClientId(user.getId())
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada."));
    }

    @Transactional
    public DepositResponseDTO executeDeposit(User user, DepositRequestDTO requestDTO) {

        Account account = getAccountByUser(user);

        if (account.getStatus() != AccountStatus.ATIVA) {
            throw new TransactionNotAuthorizedException("Conta não está ativa.");
        }

        account.credit(requestDTO.amount());

        Transaction transaction = new Transaction();
        transaction.setAccountDestiny(account);
        transaction.setAmount(requestDTO.amount());
        transaction.setType(TransactionType.DEPOSITO);
        transaction.setDescription(
                (requestDTO.description() == null || requestDTO.description().isBlank()
                        ? "Depósito": requestDTO.description())
        );

        accountRepository.save(account);
        transactionRepository.save(transaction);

        return transactionMapper.toDepositResponse(transaction);
    }

    @Transactional
    public WithdrawResponseDTO executeWithdraw(User user, WithdrawRequestDTO requestDTO) {

        Account account = getAccountByUser(user);

        if (account.getStatus() != AccountStatus.ATIVA) {
            throw new TransactionNotAuthorizedException("Conta não está ativa.");
        }

        account.debit(requestDTO.amount());

        Transaction transaction = new Transaction();
        transaction.setAccountOrigin(account);
        transaction.setAmount(requestDTO.amount());
        transaction.setType(TransactionType.SAQUE);
        transaction.setDescription(
                (requestDTO.description() == null || requestDTO.description().isBlank()
                        ? "Saque": requestDTO.description())
        );

        accountRepository.save(account);
        transactionRepository.save(transaction);

        return transactionMapper.toWithdrawResponse(transaction);
    }


}