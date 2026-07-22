package com.quantumbanking.infra.resilience;

import com.quantumbanking.infra.config.TransactionProcessingStreamConfig;
import com.quantumbanking.infra.worker.TransactionProcessingWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionProcessingWatchdog {

    private final TransactionProcessingStreamConfig transactionProcessingStreamConfig;
    private final TransactionProcessingWorker worker;

    @Scheduled(fixedRate = 30_000)
    public void checkAndRecoverSubscription() {

        if (transactionProcessingStreamConfig.isSubscriptionActive()) return;

        log.warn("Subscription do stream de processamento de transações está inativa. Tentando recriar...");

        try {
            transactionProcessingStreamConfig.createConsumerGroup();
            transactionProcessingStreamConfig.subscribe(worker);

            log.info("Subscription recriada com sucesso. Worker voltou a processar transações.");
        } catch (Exception e) {
            log.error("Falha ao tentar recriar a subscription. Nova tentativa em 30s.", e);
        }
    }
}
