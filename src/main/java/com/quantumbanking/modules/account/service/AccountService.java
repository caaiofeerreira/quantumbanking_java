package com.quantumbanking.modules.account.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quantumbanking.infra.exception.AccountNotFoundException;
import com.quantumbanking.infra.exception.TransactionNotAuthorizedException;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.AccountStatus;
import com.quantumbanking.modules.account.dto.StatementResponseDTO;
import com.quantumbanking.modules.account.repository.AccountRepository;
import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.mapper.TransactionMapper;
import com.quantumbanking.modules.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    private final ObjectMapper objectMapper;
    private final TransactionMapper transactionMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional(readOnly = true)
    @Cacheable(value = "balance", key = "#user.id")
    public BigDecimal getBalance(User user) {

        return accountRepository.findBalanceByClientId(user.getId())
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada."));
    }

    @Transactional(readOnly = true)
    public StatementResponseDTO getStatement(User user, Integer month, Integer year) {

        String cacheKey = "statement:" + user.getId() + ":" + month + ":" + year;

        StatementResponseDTO cached = getFromCache(cacheKey);
        if (cached != null) return cached;

        Account account = accountRepository.findByClientId(user.getId())
                .orElseThrow(() -> new AccountNotFoundException("Conta não encontrada."));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new TransactionNotAuthorizedException("Conta não está ativa.");
        }

        List<Transaction> transactions = transactionRepository.findByAccountAndPeriod(account.getId(), month, year);

        StatementResponseDTO result = new StatementResponseDTO(
                month, year, account.getBalance(),
                transactions.stream().map(t -> transactionMapper.toStatementResponse(t, account)).toList()
        );

        saveToCache(cacheKey, result);

        return result;
    }

    private StatementResponseDTO getFromCache(String key) {
        try {
            String cachedJson = (String) redisTemplate.opsForValue().get(key);
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
}