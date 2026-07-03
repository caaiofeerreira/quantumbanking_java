package com.quantumbanking.modules.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantumbanking.infra.exception.*;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.AccountStatus;
import com.quantumbanking.modules.account.domain.AccountType;
import com.quantumbanking.modules.account.dto.AccountResponseDTO;
import com.quantumbanking.modules.account.dto.AccountSummaryDTO;
import com.quantumbanking.modules.account.dto.StatementResponseDTO;
import com.quantumbanking.modules.account.factory.AccountFactory;
import com.quantumbanking.modules.account.mapper.AccountMapper;
import com.quantumbanking.modules.account.repository.AccountRepository;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.client.repository.ClientRepository;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.mapper.TransactionMapper;
import com.quantumbanking.modules.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountFactory accountFactory;

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
            log.warn("Falha ao ler cache para a chave: {}. Seguindo para o banco. Erro: {}", key, e.getMessage());
        }
        return null;
    }

    private void saveToCache(String key, StatementResponseDTO data) {

        try {
            String json = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(10));
        } catch (Exception e) {
            log.error("Erro ao salvar cache para a chave: {}", key, e);
        }
    }

    @Transactional
    public Account openInitialAccount(ClientType clientType, AccountType accountType, Agency agency, Client client) {

        Account account = accountFactory.createDefaultAccount(
                clientType,
                accountType,
                agency,
                client
        );

        save(account);
        return account;
    }

    @Transactional
    public AccountResponseDTO openAccount(Long userId, AccountType accountType) {

        Client client = clientRepository.findById(userId)
                .orElseThrow(() -> new ClientNotFoundException("Cliente não encontrado."));

        List<Account> existingAccounts = accountRepository.findByClientId(userId);
        if (existingAccounts.isEmpty()) {
            throw new AccountNotFoundException("Nenhuma conta encontrada para o cliente.");
        }

        Agency agency = existingAccounts.get(0).getAgency();

        Account account = accountFactory.createDefaultAccount(
                client.getType(),
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
        String cached = redisTemplate.opsForValue().get(cacheKey);

        if (cached != null) {
            getAuthenticatedUserAccount(userId, accountNumber);
            return new BigDecimal(cached);
        }

        Account account = getAuthenticatedUserAccount(userId, accountNumber);
        BigDecimal balance = account.getBalance();
        redisTemplate.opsForValue().set(cacheKey, balance.toString(), Duration.ofMinutes(10));

        return balance;
    }

    @Transactional(readOnly = true)
    public StatementResponseDTO getStatement(Long userId, String accountNumber, Integer month, Integer year) {

        Account account = getAuthenticatedUserAccount(userId, accountNumber);

        String cacheKey = "statement:" + accountNumber + ":" + month + ":" + year;

        StatementResponseDTO cached = getFromCache(cacheKey);
        if (cached != null) return cached;

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionNotAuthorizedException("Conta não está ativa.");
        }

        List<Transaction> transactions = transactionRepository.findByAccountAndPeriod(
                account.getId(),
                month,
                year
        );

        StatementResponseDTO result = new StatementResponseDTO(
                month,
                year,
                account.getBalance(),
                transactions
                        .stream()
                        .map(t -> transactionMapper.toStatementResponse(t, account)).toList()
        );

        saveToCache(cacheKey, result);

        return result;
    }
}