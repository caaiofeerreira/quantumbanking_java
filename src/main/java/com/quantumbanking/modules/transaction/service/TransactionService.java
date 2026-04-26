package com.quantumbanking.modules.transaction.service;

import com.quantumbanking.infra.exception.AccountNotFoundException;
import com.quantumbanking.infra.exception.TransactionNotAuthorizedException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.AccountStatus;
import com.quantumbanking.modules.account.domain.PixKey;
import com.quantumbanking.modules.account.repository.AccountRepository;
import com.quantumbanking.modules.account.repository.PixKeyRepository;
import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.domain.TransactionType;
import com.quantumbanking.modules.transaction.dto.*;
import com.quantumbanking.modules.transaction.formater.TransactionStatementFormatter;
import com.quantumbanking.modules.transaction.mapper.TransactionMapper;
import com.quantumbanking.modules.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PixKeyRepository pixKeyRepository;

    private final TransactionMapper transactionMapper;

    private final TransactionStatementFormatter transactionStatementFormatter;

    @Value("${bank.code}")
    private String bankCode;

    private Account getAccountByUser(User user) {

        return accountRepository.findByClientId(user.getId())
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada."));
    }

    @Transactional
    public DepositResponseDTO executeDeposit(User user, DepositRequestDTO requestDTO) {

        Account account = getAccountByUser(user);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionNotAuthorizedException("Conta não está ativa.");
        }

        Transaction transaction = new Transaction();
        transaction.setAccountDestiny(account);
        transaction.setAmount(requestDTO.amount());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setDescription(
                transactionStatementFormatter.getDisplayDescription(transaction, true));

        account.credit(requestDTO.amount());

        accountRepository.save(account);
        transactionRepository.save(transaction);

        return transactionMapper.toDepositResponse(transaction);
    }

    @Transactional
    public WithdrawResponseDTO executeWithdraw(User user, WithdrawRequestDTO requestDTO) {

        Account account = getAccountByUser(user);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionNotAuthorizedException("Conta não está ativa.");
        }

        Transaction transaction = new Transaction();
        transaction.setAccountOrigin(account);
        transaction.setAmount(requestDTO.amount());
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setDescription(
                transactionStatementFormatter.getDisplayDescription(transaction, true)
        );

        account.debit(requestDTO.amount());

        accountRepository.save(account);
        transactionRepository.save(transaction);

        return transactionMapper.toWithdrawResponse(transaction);
    }

    @Transactional
    public InternalTransactionResponseDTO executeInternalTransaction(User user, InternalTransactionRequestDTO requestDTO) {

        Account account = getAccountByUser(user);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionNotAuthorizedException("Conta não está ativa.");
        }

        Account accountDestiny = accountRepository.findByAccountNumber(requestDTO.accountNumber())
                .orElseThrow(() -> new TransactionNotAuthorizedException("Conta de destino não encontrada."));

        if (accountDestiny.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionNotAuthorizedException("Conta de destino não está ativa.");
        }

        if (account.getId().equals(accountDestiny.getId())) {
            throw new TransactionNotAuthorizedException("Não é possível transferir para a própria conta.");
        }

        Transaction transaction = new Transaction();
        transaction.setAccountOrigin(account);
        transaction.setAccountDestiny(accountDestiny);
        transaction.setDestinyName(accountDestiny.getClient().getName());
        transaction.setAmount(requestDTO.amount());
        transaction.setDestinyAgency(requestDTO.agencyNumber());
        transaction.setType(TransactionType.INTERNAL_TRANSFER);
        transaction.setDescription(
                transactionStatementFormatter.getDisplayDescription(transaction, true));

        account.debit(requestDTO.amount());
        accountDestiny.credit(requestDTO.amount());

        accountRepository.save(account);
        accountRepository.save(accountDestiny);
        transactionRepository.save(transaction);

        return transactionMapper.toInternalResponse(transaction);
    }

    @Transactional
    public ExternalTransactionResponseDTO executeExternalTransaction(User user, ExternalTransactionRequestDTO requestDTO) {

        Account account = getAccountByUser(user);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionNotAuthorizedException("Conta não está ativa.");
        }

        if (account.getAccountNumber().equals(requestDTO.destinyAccount())
                && requestDTO.bankCode().equals(bankCode)) {
            throw new TransactionNotAuthorizedException("Não é possível transferir para a própria conta.");
        }

        Transaction transaction = new Transaction();
        transaction.setAccountOrigin(account);
        transaction.setDestinyName(requestDTO.destinyName());
        transaction.setDestinyAccount(requestDTO.destinyAccount());
        transaction.setDestinyAgency(requestDTO.destinyAgency());
        transaction.setBankCode(requestDTO.bankCode());
        transaction.setDestinyDocument(requestDTO.destinyDocument());
        transaction.setAmount(requestDTO.amount());
        transaction.setType(TransactionType.EXTERNAL_TRANSFER);
        transaction.setDescription(
                transactionStatementFormatter.getDisplayDescription(transaction, true));

        account.debit(requestDTO.amount());

        accountRepository.save(account);
        transactionRepository.save(transaction);

        return transactionMapper.toExternalResponse(transaction);
    }

    @Transactional
    public PixTransactionResponseDTO executePixTransaction(User user, PixTransactionRequestDTO requestDTO) {

        Account account = getAccountByUser(user);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionNotAuthorizedException("Conta não está ativa.");
        }

        Transaction transaction = new Transaction();
        transaction.setAccountOrigin(account);
        transaction.setAmount(requestDTO.amount());
        transaction.setType(TransactionType.PIX);
        transaction.setDescription(
                transactionStatementFormatter.getDisplayDescription(transaction, true));

        Optional<PixKey> pixKey = pixKeyRepository.findByKey(requestDTO.key());

        if (pixKey.isPresent()) {
            Account accountDestiny = pixKey.get().getAccount();

            if (account.getId().equals(accountDestiny.getId())) {
                throw new TransactionNotAuthorizedException("Não é possível fazer Pix para a própria conta.");
            }

            accountDestiny.credit(requestDTO.amount());
            transaction.setAccountDestiny(accountDestiny);
            accountRepository.save(accountDestiny);
        } else {
            transaction.setDestinyDocument(requestDTO.key());
            transaction.setDestinyName(requestDTO.key());
        }

        account.debit(requestDTO.amount());

        accountRepository.save(account);
        transactionRepository.save(transaction);

        return transactionMapper.toPixResponse(transaction);
    }

}