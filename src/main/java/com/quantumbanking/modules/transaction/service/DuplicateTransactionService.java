package com.quantumbanking.modules.transaction.service;

import com.quantumbanking.infra.exception.DuplicateTransactionException;
import com.quantumbanking.modules.transaction.domain.TransactionType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DuplicateTransactionService {

    private final StringRedisTemplate redisTemplate;

    @Value("${transaction.duplicate-seconds}")
    private int duplicateSeconds;

    public void checkAndRegister(Long userId, TransactionType type, BigDecimal amount, String target) {

        String hash = buildHash(userId, type.name(), amount, target);

        Boolean isNew = redisTemplate.opsForValue()
                .setIfAbsent(hash, "1", duplicateSeconds, TimeUnit.SECONDS);

        if (Boolean.FALSE.equals(isNew)) {
            throw new DuplicateTransactionException("Transação duplicada detectada. Aguarde alguns segundos.");
        }
    }

    private String buildHash(Long userId, String type, BigDecimal amount, String target) {

        String raw = userId + "|" + type + "|" + amount.toPlainString() + "|" + target;
        return DigestUtils.sha256Hex(raw);
    }
}
