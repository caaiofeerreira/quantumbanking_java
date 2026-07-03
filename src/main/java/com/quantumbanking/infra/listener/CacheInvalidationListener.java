package com.quantumbanking.infra.listener;

import com.quantumbanking.infra.event.TransactionCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheInvalidationListener {

    private final StringRedisTemplate redisTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTransactionEvent(TransactionCompletedEvent event) {
        for (String accountNumber : event.accountNumbers()) {
            clearCache("statement:" + accountNumber + ":*");
            clearCache("balance::" + accountNumber);
        }
    }

    private void clearCache(String pattern) {
        Set<String> keys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keysFound = new HashSet<>();
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(pattern).count(100).build())) {
                while (cursor.hasNext()) {
                    keysFound.add(new String(cursor.next()));
                }
            } catch (Exception e) {
                log.error("Erro ao escanear chaves no Redis com o padrão: '{}'. O cache pode não ter sido invalidado.", pattern, e);
            }
            return keysFound;
        });

        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}