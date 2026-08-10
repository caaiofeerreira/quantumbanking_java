package com.quantumbanking.modules.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantumbanking.infra.exception.*;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.AccountStatus;
import com.quantumbanking.modules.account.domain.AccountType;
import com.quantumbanking.modules.account.dto.*;
import com.quantumbanking.modules.account.factory.AccountFactory;
import com.quantumbanking.modules.account.generator.AccountNumberGenerator;
import com.quantumbanking.modules.account.mapper.AccountMapper;
import com.quantumbanking.modules.account.repository.AccountRepository;
import com.quantumbanking.modules.account.service.validation.AccountValidator;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.client.domain.Company;
import com.quantumbanking.modules.client.repository.ClientRepository;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.domain.TransactionStatus;
import com.quantumbanking.modules.transaction.dto.TransactionStatementDTO;
import com.quantumbanking.modules.transaction.mapper.TransactionMapper;
import com.quantumbanking.modules.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountFactory accountFactory;
    private final AccountValidator accountValidator;
    private final AccountNumberGenerator accountNumberGenerator;

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;
    private final ObjectMapper objectMapper;

    private final StringRedisTemplate redisTemplate;

    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada."));
    }

    public Account getAuthenticatedUserAccount(Long userId, String accountNumber) {

        Account account = getAccountByNumber(accountNumber);

        if (!account.getClient().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Conta não pertence ao usuário autenticado.");
        }

        return account;
    }

    public Account getAccountForUpdate(Long userId, String accountNumber) {

        Account account = accountRepository.findByAccountNumberWithLock(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada."));

        if (!account.getClient().getId().equals(userId)) {
            throw new UnauthorizedAccessException("Conta não pertence ao usuário autenticado.");
        }

        return account;
    }

    public Account getByIdWithLock(Long id) {
        return accountRepository.findByIdWithLock(id)
                .orElseThrow(() -> new AccountNotFoundException ("Conta não encontrada: "+ id));
    }

    public List<Account> getAccountsByAgencyId(Long agencyId) {
        return accountRepository.findByAgencyId(agencyId);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

    private StatementResponseDTO getFromCache(String key) {

        try {
            String cachedJson = redisTemplate.opsForValue().get(key);
            if (cachedJson != null) {
                return objectMapper.readValue(cachedJson, StatementResponseDTO.class);
            }
        } catch (Exception e) {
            log.warn("Falha ao ler cache para a chave: {}. " +
                    "Seguindo para o banco. Erro: {}", key, e.getMessage());
        }
        return null;
    }

    private void saveToCache(String key, StatementResponseDTO data) {

        try {
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(10));
        } catch (Exception e) {
            log.warn("Falha ao salvar cache para a chave {}. " +
                    "Motivo: {}", key, e.getMessage());
        }
    }

    @Transactional
    public Account openInitialAccount(ClientType clientType, AccountType accountType, Agency agency, Client client, Company company) {

        accountValidator.validateAccount(clientType, accountType, client, company);

        String accountNumber = accountNumberGenerator.generate();

        Account account = accountFactory.createDefaultAccount(
                accountNumber,
                accountType,
                agency,
                client
        );

        save(account);
        return account;
    }

    @Transactional
    public AccountResponseDTO openComplementaryAccount(Long userId, AccountType accountType) {

        Client client = clientRepository.findById(userId)
                .orElseThrow(() -> new ClientNotFoundException("Cliente não encontrado."));

        List<Account> existingAccounts = accountRepository.findByClientId(userId);

        if (existingAccounts.isEmpty()) {
            throw new AccountNotFoundException("Nenhuma conta encontrada para o cliente.");
        }

        accountValidator.validateAccount(client.getType(),accountType, client, null);

        Agency agency = existingAccounts.get(0).getAgency();

        String accountNumber = accountNumberGenerator.generate();

        Account account = accountFactory.createDefaultAccount(
                accountNumber,
                accountType,
                agency,
                client
        );

        save(account);
        return accountMapper.toAccountResponseDTO(account);
    }

    @Transactional(readOnly = true)
    public List<AccountSummaryDTO> getMyAccounts(Long userId) {

        List<Account> accounts = accountRepository.findByClientId(userId);
        return accounts
                .stream()
                .map(accountMapper::toAccountSummaryDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long userId, String accountNumber) {

        String cacheKey = "balance::" + accountNumber;

        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);

            if (cached != null) {
                getAuthenticatedUserAccount(userId, accountNumber);
                return new BigDecimal(cached);
            }
        } catch (Exception e) {
            log.warn("Redis indisponível ao buscar saldo da conta {}. " +
                    "Buscando do banco de dados. Motivo: {}", accountNumber, e.getMessage());
        }

        Account account = getAuthenticatedUserAccount(userId, accountNumber);
        BigDecimal balance = account.getBalance();

        try {
            redisTemplate.opsForValue().set(cacheKey, balance.toString(), Duration.ofMinutes(10));
        } catch (Exception e) {
            log.warn("Redis indisponível ao salvar saldo da conta {} no cache. " +
                    "Motivo: {}", accountNumber, e.getMessage());
        }

        return balance;
    }

    @Transactional(readOnly = true)
    public StatementResponseDTO getStatement(Long userId, String accountNumber, Integer month, Integer year ) {

        Account account = getAuthenticatedUserAccount(userId, accountNumber);
        return buildStatementResponse(account, month, year);
    }

    @Transactional(readOnly = true)
    public StatementResponseDTO getStatementForManager(String accountNumber, Integer month, Integer year) {

        Account account = getAccountByNumber(accountNumber);
        return buildStatementResponse(account, month, year);
    }

    @Transactional(readOnly = true)
    public StatementResponseDTO buildStatementResponse(Account account, Integer month, Integer year) {

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new TransactionNotAuthorizedException("Conta encerrada. Extrato não disponível.");
        }

        String cacheKey = "statement:" + account.getAccountNumber() + ":" + month + ":" + year;

        try {
            StatementResponseDTO cached = getFromCache(cacheKey);
            if (cached != null) return cached;
        } catch (Exception e) {
            log.warn("Redis indisponível ao buscar extrato no cache para a conta {} ({}/{}). " +
                            "Buscando do banco de dados. Motivo: {}",
                    account.getAccountNumber(), month, year, e.getMessage());
        }

        List<Transaction> transactions = transactionRepository.findByAccountAndPeriod(
                account.getId(),
                month,
                year
        );

        List<TransactionStatementDTO> mappedTransactions = transactions.stream()
                .map(t -> transactionMapper.toStatementResponse(t, account))
                .toList();

        StatementSummaryDTO summary = calculateSummary(mappedTransactions);

        StatementResponseDTO statement = new StatementResponseDTO(
                month,
                year,
                account.getBalance(),
                summary,
                mappedTransactions
        );

        try {
            saveToCache(cacheKey, statement);
        } catch (Exception e) {
            log.warn("Redis indisponível ao salvar extrato no cache para a conta {}. " +
                    "Motivo: {}", account.getAccountNumber(), e.getMessage());
        }

        return statement;
    }

    @Transactional(readOnly = true)
    public MultiMonthStatementResponseDTO buildLastThreeMonthsStatement(Long userId, String accountNumber) {

        Account account = getAuthenticatedUserAccount(userId, accountNumber);

        LocalDate today = LocalDate.now();

        List<MonthlyStatementDTO> months = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            LocalDate date = today.minusMonths(i);
            YearMonth yearMonth = YearMonth.from(date);

            int month = yearMonth.getMonthValue();
            int year = yearMonth.getYear();

            StatementResponseDTO monthlyStatement = buildStatementResponse(account, month, year);

            months.add(new MonthlyStatementDTO(
                    month,
                    year,
                    monthlyStatement.summary(),
                    monthlyStatement.transactions())
            );
        }
        return new MultiMonthStatementResponseDTO(account.getBalance(), months);
    }

    private StatementSummaryDTO calculateSummary(List<TransactionStatementDTO> transactions) {

        List<TransactionStatementDTO> completed = transactions.stream()
                .filter(t -> t.status() == TransactionStatus.COMPLETED)
                .toList();

        BigDecimal totalIn = completed.stream()
                .map(TransactionStatementDTO::amount)
                .filter(amount -> amount.signum() >0)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal totalOut = completed.stream()
                .map(TransactionStatementDTO::amount)
                .filter(amount -> amount.signum() < 0)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .abs();

        return new StatementSummaryDTO(totalIn, totalOut);
    }
}