package com.quantumbanking.infra.async.transaction;

import com.quantumbanking.infra.config.TransactionProcessingStreamConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionSubscriptionWatchdog {

    private final TransactionProcessingStreamConfig transactionProcessingStreamConfig;
    private final TransactionProcessingListener transactionProcessingListener;

    @Scheduled(fixedRateString = "${transaction.watchdog.check-rate-ms}")
    public void checkAndRecoverSubscription() {

        if (transactionProcessingStreamConfig.isSubscriptionActive()) return;

        log.warn("Subscription do stream de processamento de transações está inativa. Tentando recriar...");

        try {
            transactionProcessingStreamConfig.createConsumerGroup();
            transactionProcessingStreamConfig.subscribe(transactionProcessingListener);

            log.info("Subscription recriada com sucesso. Listener voltou a processar transações.");
        } catch (Exception e) {
            log.error("Falha ao tentar recriar a subscription. Nova tentativa no próximo ciclo.", e);
        }
    }
}