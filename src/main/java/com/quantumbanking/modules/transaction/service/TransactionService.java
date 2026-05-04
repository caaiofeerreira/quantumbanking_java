package com.quantumbanking.modules.transaction.service;

import com.quantumbanking.infra.event.TransactionCompletedEvent;
import com.quantumbanking.infra.exception.TransactionNotAuthorizedException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.AccountStatus;
import com.quantumbanking.modules.account.domain.PixKey;
import com.quantumbanking.modules.account.repository.AccountRepository;
import com.quantumbanking.modules.account.repository.PixKeyRepository;
import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.shared.service.UserService;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.domain.TransactionType;
import com.quantumbanking.modules.transaction.dto.*;
import com.quantumbanking.modules.transaction.factory.TransactionFactory;
import com.quantumbanking.modules.transaction.mapper.TransactionMapper;
import com.quantumbanking.modules.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PixKeyRepository pixKeyRepository;

    private final TransactionFactory transactionFactory;

    private final TransactionMapper transactionMapper;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final UserService userService;

    @Value("${bank.code}")
    private String bankCode;

    @Transactional
    public DepositResponseDTO executeDeposit(User user, DepositRequestDTO requestDTO) {

        Account account = userService
                .getAuthenticatedUserAccount(user.getId());

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionNotAuthorizedException("Conta não está ativa.");
        }

        Set<Long> usersToInvalidate = Set.of(user.getId());

        Transaction transaction = transactionFactory
                .createDeposit(
                        account,
                        requestDTO.amount(),
                        requestDTO.description()
                );

        account.credit(requestDTO.amount());

        accountRepository.save(account);
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(new TransactionCompletedEvent(usersToInvalidate));

        return transactionMapper.toDepositResponse(transaction);
    }

    @Transactional
    public WithdrawResponseDTO executeWithdraw(User user, WithdrawRequestDTO requestDTO) {

        Account account = userService
                .getAuthenticatedUserAccount(user.getId());

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionNotAuthorizedException("Conta não está ativa.");
        }

        Set<Long> usersToInvalidate = Set.of(user.getId());

        Transaction transaction = transactionFactory
                .createWithdrawal(
                        account,
                        requestDTO.amount(),
                        requestDTO.description()
                );

        account.debit(requestDTO.amount());

        accountRepository.save(account);
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(new TransactionCompletedEvent(usersToInvalidate));

        return transactionMapper.toWithdrawResponse(transaction);
    }

    @Transactional
    public InternalTransactionResponseDTO executeInternalTransaction(User user, InternalTransactionRequestDTO requestDTO) {

        Account originAccount = userService
                .getAuthenticatedUserAccount(user.getId());

        if (originAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionNotAuthorizedException("Conta não está ativa.");
        }

        Account destinyAccount = accountRepository.findByAccountNumber(requestDTO.accountNumber())
                .orElseThrow(() -> new TransactionNotAuthorizedException("Conta de destino não encontrada."));

        if (destinyAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionNotAuthorizedException("Conta de destino não está ativa.");
        }

        if (originAccount.getId().equals(destinyAccount.getId())) {
            throw new TransactionNotAuthorizedException("Não é possível transferir para a própria conta.");
        }

        Set<Long> usersToInvalidate = new HashSet<>();
        usersToInvalidate.add(user.getId());
        usersToInvalidate.add(destinyAccount.getClient().getId());

        Transaction transaction = transactionFactory
                .createInternalTransfer(
                        originAccount,
                        destinyAccount,
                        requestDTO.agencyNumber(),
                        requestDTO.amount(),
                        requestDTO.description()
                );

        originAccount.debit(requestDTO.amount());
        destinyAccount.credit(requestDTO.amount());

        accountRepository.save(originAccount);
        accountRepository.save(destinyAccount);
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(new TransactionCompletedEvent(usersToInvalidate));

        return transactionMapper.toInternalResponse(transaction);
    }

    @Transactional
    public ExternalTransactionResponseDTO executeExternalTransaction(User user, ExternalTransactionRequestDTO requestDTO) {

        Account account = userService
                .getAuthenticatedUserAccount(user.getId());

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionNotAuthorizedException("Conta não está ativa.");
        }

        if (account.getAccountNumber().equals(requestDTO.destinyAccount())
                && requestDTO.bankCode().equals(bankCode)) {
            throw new TransactionNotAuthorizedException("Não é possível transferir para a própria conta.");
        }

        Set<Long> usersToInvalidate = Set.of(user.getId());

        Transaction transaction = transactionFactory
                .createExternalTransfer(
                        account,
                        requestDTO.destinyAccount(),
                        requestDTO.destinyName(),
                        requestDTO.destinyAgency(),
                        requestDTO.bankCode(),
                        requestDTO.destinyDocument(),
                        requestDTO.amount(),
                        requestDTO.description()
                );

        account.debit(requestDTO.amount());

        accountRepository.save(account);
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(new TransactionCompletedEvent(usersToInvalidate));

        return transactionMapper.toExternalResponse(transaction);
    }

    @Transactional
    public PixTransactionResponseDTO executePixTransaction(User user, PixTransactionRequestDTO requestDTO) {

        Account originAccount = userService.getAuthenticatedUserAccount(user.getId());

        Optional<PixKey> pixKey = pixKeyRepository.findByKey(requestDTO.key());
        Account destinyAccount = pixKey.map(PixKey::getAccount).orElse(null);

        if (destinyAccount != null && originAccount.getId().equals(destinyAccount.getId())) {
            throw new TransactionNotAuthorizedException("Não é possível fazer Pix para a própria conta.");
        }

        Transaction transaction = transactionFactory
                .createPix(
                        originAccount,
                        requestDTO.amount(),
                        requestDTO.description(),
                        requestDTO.key(),
                        destinyAccount
                );

        originAccount.debit(requestDTO.amount());

        Set<Long> usersToInvalidate = new HashSet<>();
        usersToInvalidate.add(user.getId());

        if (destinyAccount != null) {
            destinyAccount.credit(requestDTO.amount());
            accountRepository.save(destinyAccount);
            usersToInvalidate.add(destinyAccount.getClient().getId());
        }

        accountRepository.save(originAccount);
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(new TransactionCompletedEvent(usersToInvalidate));

        return transactionMapper.toPixResponse(transaction);
    }
}