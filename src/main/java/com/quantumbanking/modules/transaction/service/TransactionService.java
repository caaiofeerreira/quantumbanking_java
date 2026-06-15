package com.quantumbanking.modules.transaction.service;

import com.quantumbanking.infra.event.TransactionCompletedEvent;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.PixKey;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.account.service.PixKeyService;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.bank.domain.bank.BankRegistry;
import com.quantumbanking.modules.bank.service.AgencyService;
import com.quantumbanking.modules.bank.service.BankRegistryService;
import com.quantumbanking.modules.bank.service.BankService;
import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.domain.TransactionType;
import com.quantumbanking.modules.transaction.dto.*;
import com.quantumbanking.modules.transaction.factory.TransactionFactory;
import com.quantumbanking.modules.transaction.mapper.TransactionMapper;
import com.quantumbanking.modules.transaction.repository.TransactionRepository;
import com.quantumbanking.modules.transaction.service.validation.TransactionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountService accountService;
    private final PixKeyService pixKeyService;
    private final AgencyService agencyService;
    private final BankRegistryService bankRegistryService;
    private final DuplicateTransactionService duplicateTransactionService;
    private final BankService bankService;

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionFactory transactionFactory;
    private final TransactionValidator transactionValidator;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Value("${transaction.timezone}")
    private String timezone;

    @Transactional
    public DepositResponseDTO executeDeposit(Long userId, String accountNumber, DepositRequestDTO requestDTO) {

        Account account = accountService.getAccountForUpdate(userId, accountNumber);

        transactionValidator.validateDeposit(account, requestDTO.amount());

        duplicateTransactionService.checkAndRegister(
                userId,
                TransactionType.DEPOSIT,
                requestDTO.amount(),
                "self"
        );

        Transaction transaction = transactionFactory
                .createDeposit(
                        account,
                        requestDTO.amount(),
                        requestDTO.description()
                );

        account.credit(requestDTO.amount());

        accountService.save(account);
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(new TransactionCompletedEvent(account.getAccountNumber()));

        return transactionMapper.toDepositResponse(transaction);
    }

    @Transactional
    public WithdrawResponseDTO executeWithdraw(Long userId, String accountNumber, WithdrawRequestDTO requestDTO) {

        Account account = accountService.getAccountForUpdate(userId, accountNumber);

        LocalDateTime start = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1);

        long withdrawalsThisMonth = transactionRepository.countByOriginAccountAndTypeAndPeriod(
                account.getId(),
                TransactionType.WITHDRAWAL,
                start,
                end
        );
        int freeWithdrawals = account.getType().getFreeWithdrawals();
        boolean shouldChargeFee = withdrawalsThisMonth >= freeWithdrawals;

        transactionValidator.validateWithdraw(account, requestDTO.amount(), shouldChargeFee);

        duplicateTransactionService.checkAndRegister(
                userId,
                TransactionType.WITHDRAWAL,
                requestDTO.amount(),
                "self"
        );

        FeeDetailDTO fee;

        if (shouldChargeFee) {

            BigDecimal feeAmount = account.getType().getFeeAmount();
            Transaction feeTransaction = transactionFactory.createFee(account, feeAmount);
            account.debit(feeAmount);
            bankService.creditFee(feeAmount);
            transactionRepository.save(feeTransaction);

            fee = new FeeDetailDTO(true, feeAmount,
                    "Limite mensal de saques gratuitos atingido (%d/%d utilizados)"
                            .formatted(withdrawalsThisMonth + 1, freeWithdrawals));
        } else {
            fee = new FeeDetailDTO(false, BigDecimal.ZERO,
                    "Dentro do limite mensal de saques gratuitos (%d/%d utilizados)"
                            .formatted(withdrawalsThisMonth + 1, freeWithdrawals));
        }

        Transaction transaction = transactionFactory
                .createWithdrawal(
                        account,
                        requestDTO.amount(),
                        requestDTO.description()
                );

        account.debit(requestDTO.amount());

        accountService.save(account);
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(new TransactionCompletedEvent(account.getAccountNumber()));

        return transactionMapper.toWithdrawResponse(transaction, fee);
    }

    @Transactional
    public InternalTransactionResponseDTO executeInternalTransaction(User user, String accountNumber, InternalTransactionRequestDTO requestDTO) {

        Account originAccount = accountService.getAccountByNumber(accountNumber);
        Account destinationAccount = accountService.getAccountByNumber(requestDTO.destinationAccountNumber());

        AccountPair accounts = lockAccountsInOrder(originAccount.getId(), destinationAccount.getId());
        originAccount = accounts.originAccount();
        destinationAccount = accounts.destinationAccount();

        Agency agency = agencyService.getAgencyByNumber(requestDTO.agencyNumber());

        transactionValidator.validateInternal(
                originAccount,
                destinationAccount,
                agency,
                requestDTO.amount(),
                user.getId()
        );

        duplicateTransactionService.checkAndRegister(
                user.getId(),
                TransactionType.INTERNAL_TRANSFER,
                requestDTO.amount(),
                requestDTO.destinationAccountNumber()
        );

        Transaction transaction = transactionFactory
                .createInternalTransfer(
                        originAccount,
                        destinationAccount,
                        requestDTO.agencyNumber(),
                        requestDTO.amount(),
                        requestDTO.description()
                );

        originAccount.debit(requestDTO.amount());
        destinationAccount.credit(requestDTO.amount());

        accountService.save(originAccount);
        accountService.save(destinationAccount);
        transactionRepository.save(transaction);

        Set<String> accountsToInvalidate = Set.of(
                originAccount.getAccountNumber(),
                destinationAccount.getAccountNumber()
        );
        applicationEventPublisher.publishEvent(new TransactionCompletedEvent(accountsToInvalidate));

        return transactionMapper.toInternalResponse(transaction);
    }

    @Transactional
    public ExternalTransactionResponseDTO executeExternalTransaction(User user, String accountNumber, ExternalTransactionRequestDTO requestDTO) {

        Account account = accountService.getAccountForUpdate(user.getId(), accountNumber);

        transactionValidator.validateExternal(
                account,
                requestDTO.compe(),
                requestDTO.amount(),
                user.getId()
        );

        BankRegistry bankRegistry = bankRegistryService.getByCompe(requestDTO.compe());

        duplicateTransactionService.checkAndRegister(
                user.getId(),
                TransactionType.EXTERNAL_TRANSFER,
                requestDTO.amount(),
                requestDTO.destinationAccount()
        );

        Transaction transaction = transactionFactory
                .createExternalTransfer(
                        account,
                        requestDTO.destinationAccount(),
                        requestDTO.destinationName(),
                        requestDTO.destinationAgency(),
                        bankRegistry.getCompe(),
                        requestDTO.destinationDocument(),
                        bankRegistry.getName(),
                        requestDTO.amount(),
                        requestDTO.description()
                );

        account.debit(requestDTO.amount());

        accountService.save(account);
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(new TransactionCompletedEvent(account.getAccountNumber()));

        return transactionMapper.toExternalResponse(transaction);
    }

    @Transactional
    public PixTransactionResponseDTO executePixTransaction(User user, String accountNumber, PixTransactionRequestDTO requestDTO) {

        LocalTime transactionTime = LocalDateTime.now(ZoneId.of(timezone)).toLocalTime();

        Optional<PixKey> pixKey = pixKeyService.getKey(requestDTO.key());

        Account originAccount = accountService.getAccountByNumber(accountNumber);
        Account destinationAccount = pixKey
                .map(PixKey::getAccount)
                .orElse(null);

        if (destinationAccount != null) {
            AccountPair accounts = lockAccountsInOrder(originAccount.getId(), destinationAccount.getId());
            originAccount = accounts.originAccount();
            destinationAccount = accounts.destinationAccount();
        } else {
            originAccount = accountService.getByIdWithLock(originAccount.getId());
        }

        transactionValidator.validatePix(
                originAccount,
                destinationAccount,
                requestDTO.amount(),
                transactionTime,
                user.getId()
        );

        duplicateTransactionService.checkAndRegister(
                user.getId(),
                TransactionType.PIX,
                requestDTO.amount(),
                requestDTO.key()
        );

        Transaction transaction = transactionFactory
                .createPix(
                        originAccount,
                        destinationAccount,
                        requestDTO.amount(),
                        requestDTO.description(),
                        requestDTO.key()
                );

        Set<String> accountsToInvalidate = new HashSet<>();
        accountsToInvalidate.add(originAccount.getAccountNumber());

        originAccount.debit(requestDTO.amount());

        if (destinationAccount != null) {
            destinationAccount.credit(requestDTO.amount());
            accountsToInvalidate.add(destinationAccount.getAccountNumber());
            accountService.save(destinationAccount);
        }

        accountService.save(originAccount);
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(new TransactionCompletedEvent(accountsToInvalidate));

        return transactionMapper.toPixResponse(transaction);
    }

    @Transactional
    public void executeLoan(Bank bank, Account account, BigDecimal amount, String description) {

        Set<String> accountsToInvalidate = Set.of(account.getAccountNumber());

        Transaction transaction = transactionFactory.createLoan(bank, account, amount, description);
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(new TransactionCompletedEvent(accountsToInvalidate));
    }


    /// Lock em ordem crescente de ID para evitar deadlock em transferências simultâneas entre as mesmas contas (ex: A→B e B→A)
    private AccountPair lockAccountsInOrder(Long originId, Long destinationId) {

        Long firstId = Math.min(originId, destinationId);
        Long secondId = Math.max(originId, destinationId);

        Account first = accountService.getByIdWithLock(firstId);
        Account second = accountService.getByIdWithLock(secondId);

        return new AccountPair(
                originId.equals(firstId) ? first : second,
                originId.equals(firstId) ? second : first
        );
    }
}