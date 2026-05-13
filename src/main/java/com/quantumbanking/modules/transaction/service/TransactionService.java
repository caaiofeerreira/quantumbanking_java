package com.quantumbanking.modules.transaction.service;

import com.quantumbanking.infra.event.TransactionCompletedEvent;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.PixKey;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.account.service.PixKeyService;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.service.AgencyService;
import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.dto.*;
import com.quantumbanking.modules.transaction.factory.TransactionFactory;
import com.quantumbanking.modules.transaction.mapper.TransactionMapper;
import com.quantumbanking.modules.transaction.repository.TransactionRepository;
import com.quantumbanking.modules.transaction.service.validation.TransactionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountService accountService;
    private final PixKeyService pixKeyService;
    private final AgencyService agencyService;

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionFactory transactionFactory;
    private final TransactionValidator transactionValidator;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public DepositResponseDTO executeDeposit(User user, DepositRequestDTO requestDTO) {

        Account account = accountService.getAccountForUpdate(user.getId());

        transactionValidator.validateDeposit(account, requestDTO.amount());

        Set<Long> usersToInvalidate = Set.of(user.getId());

        Transaction transaction = transactionFactory
                .createDeposit(
                        account,
                        requestDTO.amount(),
                        requestDTO.description()
                );

        account.credit(requestDTO.amount());

        accountService.save(account);
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(new TransactionCompletedEvent(usersToInvalidate));

        return transactionMapper.toDepositResponse(transaction);
    }

    @Transactional
    public WithdrawResponseDTO executeWithdraw(User user, WithdrawRequestDTO requestDTO) {

        Account account = accountService.getAccountForUpdate(user.getId());

        transactionValidator.validateWithdraw(account, requestDTO.amount());

        Set<Long> usersToInvalidate = Set.of(user.getId());

        Transaction transaction = transactionFactory
                .createWithdrawal(
                        account,
                        requestDTO.amount(),
                        requestDTO.description()
                );

        account.debit(requestDTO.amount());

        accountService.save(account);
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(new TransactionCompletedEvent(usersToInvalidate));

        return transactionMapper.toWithdrawResponse(transaction);
    }

    @Transactional
    public InternalTransactionResponseDTO executeInternalTransaction(User user, InternalTransactionRequestDTO requestDTO) {

        Account originAccount = accountService.getAccountForUpdate(user.getId());
        Account destinyAccount = accountService.getAccountByNumber(requestDTO.accountNumber());

        Agency agency = agencyService.getAgencyByNumber(requestDTO.agencyNumber());

        transactionValidator.validateInternal(originAccount, destinyAccount, agency);

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

        accountService.save(originAccount);
        accountService.save(destinyAccount);
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(new TransactionCompletedEvent(usersToInvalidate));

        return transactionMapper.toInternalResponse(transaction);
    }

    @Transactional
    public ExternalTransactionResponseDTO executeExternalTransaction(User user, ExternalTransactionRequestDTO requestDTO) {

        Account account = accountService.getAccountForUpdate(user.getId());

        transactionValidator.validateExternal(account, requestDTO.destinyAccount(), requestDTO.bankCode());

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

        accountService.save(account);
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(new TransactionCompletedEvent(usersToInvalidate));

        return transactionMapper.toExternalResponse(transaction);
    }

    @Transactional
    public PixTransactionResponseDTO executePixTransaction(User user, PixTransactionRequestDTO requestDTO) {

        Account originAccount = accountService.getAccountForUpdate(user.getId());

        Optional<PixKey> pixKey = pixKeyService.findByKey(requestDTO.key());
        Account destinyAccount = pixKey.map(PixKey::getAccount).orElse(null);

        transactionValidator.validatePix(originAccount, destinyAccount);

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
            accountService.save(destinyAccount);
            usersToInvalidate.add(destinyAccount.getClient().getId());
        }

        accountService.save(originAccount);
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(new TransactionCompletedEvent(usersToInvalidate));

        return transactionMapper.toPixResponse(transaction);
    }
}