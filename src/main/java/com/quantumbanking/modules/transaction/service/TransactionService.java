package com.quantumbanking.modules.transaction.service;

import com.quantumbanking.infra.event.AccountBalanceChangedEvent;
import com.quantumbanking.infra.exception.TransactionDetailNotAvailableException;
import com.quantumbanking.infra.exception.TransactionNotFoundException;
import com.quantumbanking.infra.resilience.RedisAvailabilityGuard;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.service.AccountService;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.bank.domain.bank.BankAccount;
import com.quantumbanking.modules.bank.domain.bank.BankRegistry;
import com.quantumbanking.modules.bank.service.AgencyService;
import com.quantumbanking.modules.bank.service.BankRegistryService;
import com.quantumbanking.modules.bank.service.BankService;
import com.quantumbanking.modules.loan.domain.Loan;
import com.quantumbanking.modules.pixKey.detector.PixKeyDetector;
import com.quantumbanking.modules.pixKey.resolver.PixKeyResolver;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.domain.TransactionOutbox;
import com.quantumbanking.modules.transaction.domain.TransactionStatus;
import com.quantumbanking.modules.transaction.domain.TransactionType;
import com.quantumbanking.modules.transaction.dto.*;
import com.quantumbanking.modules.transaction.factory.TransactionFactory;
import com.quantumbanking.modules.transaction.mapper.TransactionMapper;
import com.quantumbanking.modules.transaction.repository.TransactionOutboxRepository;
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
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountService accountService;
    private final AgencyService agencyService;
    private final BankRegistryService bankRegistryService;
    private final DuplicateTransactionService duplicateTransactionService;
    private final BankService bankService;

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final TransactionFactory transactionFactory;
    private final TransactionValidator transactionValidator;

    private final PixKeyResolver pixKeyResolver;
    private final TransactionOutboxRepository transactionOutboxRepository;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final RedisAvailabilityGuard redisAvailabilityGuard;

    @Value("${transaction.timezone}")
    private String timezone;

    // Lock em ordem crescente de ID para evitar deadlock em transferências simultâneas entre as mesmas contas
    // (ex: A→B e B→A)
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

    @Transactional
    public DepositResponseDTO executeDeposit(Long userId, String accountNumber, DepositRequestDTO requestDTO) {

        redisAvailabilityGuard.ensureAvailable();

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

        applicationEventPublisher.publishEvent(new AccountBalanceChangedEvent(account.getAccountNumber()));

        return transactionMapper.toDepositResponse(transaction);
    }

    @Transactional
    public WithdrawResponseDTO executeWithdraw(Long userId, String accountNumber, WithdrawRequestDTO requestDTO) {

        redisAvailabilityGuard.ensureAvailable();

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

        transactionValidator.validateWithdraw(
                account,
                requestDTO.amount(),
                shouldChargeFee
        );

        duplicateTransactionService.checkAndRegister(
                userId,
                TransactionType.WITHDRAWAL,
                requestDTO.amount(),
                "self"
        );

        FeeDetailDTO fee;

        if (shouldChargeFee) {

            BigDecimal feeAmount = account.getType().getFeeAmount();
            Bank bank = bankService.getBank();
            Transaction feeTransaction = transactionFactory.createFee(
                    account, bank, feeAmount
            );
            account.debit(feeAmount);
            bank.getAccount().credit(feeAmount);

            bankService.save(bank.getAccount());
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

        applicationEventPublisher.publishEvent(new AccountBalanceChangedEvent(account.getAccountNumber()));

        return transactionMapper.toWithdrawResponse(transaction, fee);
    }

    @Transactional
    public InternalTransactionResponseDTO executeInternalTransaction(Long userId, String accountNumber, InternalTransactionRequestDTO requestDTO) {

        redisAvailabilityGuard.ensureAvailable();

        Account originAccount = accountService.getAuthenticatedUserAccount(userId, accountNumber);
        Account destinationAccount = accountService.getAccountByNumber(requestDTO.destinationAccountNumber());

        AccountPair accounts = lockAccountsInOrder(originAccount.getId(), destinationAccount.getId());
        originAccount = accounts.originAccount();
        destinationAccount = accounts.destinationAccount();

        Long agencyId = agencyService.getAgencyIdByNumber(requestDTO.agencyNumber());

        transactionValidator.validateInternal(
                originAccount,
                destinationAccount,
                agencyId,
                requestDTO.amount(),
                userId
        );

        duplicateTransactionService.checkAndRegister(
                userId,
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
        applicationEventPublisher.publishEvent(new AccountBalanceChangedEvent(accountsToInvalidate));

        return transactionMapper.toInternalResponse(transaction);
    }

    @Transactional
    public ExternalTransactionResponseDTO executeExternalTransaction(Long userId, String accountNumber, ExternalTransactionRequestDTO requestDTO) {

        redisAvailabilityGuard.ensureAvailable();

        Account account = accountService.getAccountForUpdate(userId, accountNumber);

        transactionValidator.validateExternal(
                account,
                requestDTO.compe(),
                requestDTO.amount(),
                userId
        );

        BankRegistry bankRegistry = bankRegistryService.getByCompe(requestDTO.compe());

        duplicateTransactionService.checkAndRegister(
                userId,
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

        applicationEventPublisher.publishEvent(new AccountBalanceChangedEvent(account.getAccountNumber()));

        return transactionMapper.toExternalResponse(transaction);
    }

    @Transactional
    public PixTransactionResponseDTO executePixTransaction(Long userId, String accountNumber, PixTransactionRequestDTO requestDTO) {

        redisAvailabilityGuard.ensureAvailable();

        LocalTime transactionTime = LocalDateTime.now(ZoneId.of(timezone)).toLocalTime();

        PixKeyDetector.PixKeyDetectionResult detection = PixKeyDetector.checkAndDetectKey(requestDTO.key());
        String normalizedKey = detection.normalizedKey();

        PixKeyResolver.PixKeyResolution resolution = pixKeyResolver.resolveKey(normalizedKey);

        Account originAccount = accountService.getAccountForUpdate(userId, accountNumber);
        Account destinationAccount = resolution.internalAccount();

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
                userId
        );

        duplicateTransactionService.checkAndRegister(
                userId,
                TransactionType.PIX,
                requestDTO.amount(),
                normalizedKey
        );

        Set<String> accountsToInvalidate = new HashSet<>();
        accountsToInvalidate.add(originAccount.getAccountNumber());

        Transaction transaction;

        if (!resolution.external()) {
            transaction = transactionFactory.createPix(
                    originAccount, destinationAccount, requestDTO.amount(), requestDTO.description(),
                    normalizedKey, detection.type(), TransactionStatus.COMPLETED,
                    null, null, null, null
            );

            originAccount.debit(requestDTO.amount());
            destinationAccount.credit(requestDTO.amount());
            accountsToInvalidate.add(destinationAccount.getAccountNumber());
            accountService.save(destinationAccount);
            accountService.save(originAccount);
            transaction = transactionRepository.save(transaction);

        } else {
            transaction = transactionFactory.createPix(
                    originAccount, null, requestDTO.amount(), requestDTO.description(),
                    normalizedKey, detection.type(), TransactionStatus.PENDING,
                    resolution.destinationBank().getCompe(),
                    resolution.destinationBank().getName(),
                    resolution.externalEntry().ownerDocument(),
                    resolution.externalEntry().ownerName()
            );

            originAccount.reserve(requestDTO.amount());
            accountService.save(originAccount);
            transaction = transactionRepository.save(transaction);

            TransactionOutbox outbox = TransactionOutbox.builder()
                    .transaction(transaction)
                    .build();
            transactionOutboxRepository.save(outbox);
        }

        applicationEventPublisher.publishEvent(new AccountBalanceChangedEvent(accountsToInvalidate));

        return transactionMapper.toPixResponse(transaction);
    }

    @Transactional
    public void executeLoan(Loan loan) {

        redisAvailabilityGuard.ensureAvailable();

        Transaction transaction = transactionFactory.createLoan(loan);

        BankAccount bankAccount = loan.getAccount().getAgency().getBank().getAccount();
        bankAccount.debit(loan.getAmount());
        loan.getAccount().credit(loan.getAmount());

        bankService.save(bankAccount);
        accountService.save(loan.getAccount());
        transactionRepository.save(transaction);

        applicationEventPublisher.publishEvent(new AccountBalanceChangedEvent(loan.getAccount().getAccountNumber()));
    }

    @Transactional(readOnly = true)
    public TransactionDetailResponse getTransactionDetail(Long userId, String accountNumber, UUID transactionId) {

        Account account = accountService.getAuthenticatedUserAccount(userId, accountNumber);

        Transaction transaction = transactionRepository.findByIdAndAccountInvolved(transactionId, account.getId())
                .orElseThrow(() -> new TransactionNotFoundException("Transação não encontrada: " + transactionId));

        return switch (transaction.getType()) {
            case DEPOSIT -> transactionMapper.toDepositResponse(transaction);
            case WITHDRAWAL -> transactionMapper.toWithdrawResponse(transaction);
            case INTERNAL_TRANSFER -> transactionMapper.toInternalResponse(transaction);
            case EXTERNAL_TRANSFER -> transactionMapper.toExternalResponse(transaction);
            case PIX -> transactionMapper.toPixResponse(transaction);
            case LOAN -> transactionMapper.toLoanTransactionDetail(transaction);
            case FEE -> throw new TransactionDetailNotAvailableException(
                    "Detalhamento não disponível para transações do tipo: " + transaction.getType());
        };
    }
}