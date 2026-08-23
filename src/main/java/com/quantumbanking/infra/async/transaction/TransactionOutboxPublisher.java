package com.quantumbanking.infra.async.transaction;

import com.quantumbanking.modules.transaction.domain.OutboxStatus;
import com.quantumbanking.modules.transaction.domain.TransactionOutbox;
import com.quantumbanking.modules.transaction.repository.TransactionOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionOutboxPublisher {

    private final TransactionOutboxRepository transactionOutboxRepository;
    private final TransactionOutboxDispatchHandler dispatchHandler;

    public static final String STREAM_KEY = "stream:transaction-processing";

    @Value("${transaction.outbox.publisher.max-retry-attempts}")
    private int maxRetryAttempts;

    @Scheduled(fixedRateString = "${transaction.outbox.publisher.fixed-rate-ms}")
    public void publishPendingOutbox() {

        List<TransactionOutbox> pending = transactionOutboxRepository
                .findByStatusAndRetryCountLessThanOrderByCreatedAtAsc(
                        OutboxStatus.PENDING_PUBLISH,
                        maxRetryAttempts
                );

        if (pending.isEmpty()) return;

        for (TransactionOutbox outbox : pending) dispatchHandler.publishSingle(outbox);
    }
}