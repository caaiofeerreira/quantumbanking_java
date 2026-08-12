package com.quantumbanking.infra.listener;

import com.quantumbanking.infra.worker.TransactionOutboxPublisherWorker;
import com.quantumbanking.modules.transaction.domain.OutboxStatus;
import com.quantumbanking.modules.transaction.domain.TransactionOutbox;
import com.quantumbanking.modules.transaction.repository.TransactionOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionOutboxPublisher {

    private final TransactionOutboxRepository transactionOutboxRepository;
    private final TransactionOutboxPublisherWorker worker;

    public static final String STREAM_KEY = "stream:transaction-processing";

    @Scheduled(fixedRateString = "${transaction.outbox.publisher.fixed-rate-ms}")
    public void publishPendingOutbox() {

        List<TransactionOutbox> pending = transactionOutboxRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING_PUBLISH);

        if (pending.isEmpty()) return;

        for (TransactionOutbox outbox : pending) worker.publishSingle(outbox);
    }
}