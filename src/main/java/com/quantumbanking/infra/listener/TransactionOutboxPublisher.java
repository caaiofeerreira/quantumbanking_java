package com.quantumbanking.infra.listener;

import com.quantumbanking.modules.transaction.domain.OutboxStatus;
import com.quantumbanking.modules.transaction.domain.TransactionOutbox;
import com.quantumbanking.modules.transaction.repository.TransactionOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionOutboxPublisher {

    private final StringRedisTemplate redisTemplate;
    private final TransactionOutboxRepository transactionOutboxRepository;
    private static final int MAX_RETRY_ATTEMPTS = 5;
    public static final String STREAM_KEY = "stream:transaction-processing";

    @Scheduled(fixedRate = 5000)
    @Transactional
    public void publishPendingOutbox() {

        List<TransactionOutbox> pending = transactionOutboxRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING_PUBLISH);

        if (pending.isEmpty()) return;

        for (TransactionOutbox outbox : pending) publishSingle(outbox);
    }

    private void publishSingle(TransactionOutbox outbox) {
        try {

            Map<String, String> payload = Map.of("transactionId", outbox.getTransaction().getId().toString());

            RecordId recordId = redisTemplate.opsForStream().add(STREAM_KEY, payload);

            outbox.markPublished(recordId.toString());
            transactionOutboxRepository.save(outbox);

            log.info("Transação enviada para processamento (outboxId={}, streamId={}): {}",
                    outbox.getId(), recordId, outbox.getTransaction().getId());

        } catch (Exception e) {
            outbox.registerFailedAttempt(e.getMessage(), MAX_RETRY_ATTEMPTS);
            transactionOutboxRepository.save(outbox);

            log.error("Erro ao publicar outbox (id={}) no stream de processamento. Tentativa {}/{}",
                    outbox.getId(), outbox.getRetryCount(), MAX_RETRY_ATTEMPTS, e);
        }
    }
}