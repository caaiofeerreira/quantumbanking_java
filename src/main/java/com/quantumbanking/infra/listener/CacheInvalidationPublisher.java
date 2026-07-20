package com.quantumbanking.infra.listener;

import com.quantumbanking.infra.event.AccountBalanceChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheInvalidationPublisher {

    private final StringRedisTemplate redisTemplate;

    public static final String STREAM_KEY = "stream:cache-invalidation";

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTransactionEvent(AccountBalanceChangedEvent event) {

        if (event.accountNumbers() == null || event.accountNumbers().isEmpty()) return;


        try {
            String accounts = String.join(",", event.accountNumbers());
            Map<String, String> payload = Map.of("accounts", accounts);

            RecordId recordId = redisTemplate.opsForStream().add(STREAM_KEY, payload);

            log.info("Contas enviadas para o stream de invalidação (id={}): {}", recordId, accounts);
        } catch (Exception e) {
            log.error("Erro ao enviar contas para o stream do Redis. Contas: {}", event.accountNumbers(), e);
        }
    }
}