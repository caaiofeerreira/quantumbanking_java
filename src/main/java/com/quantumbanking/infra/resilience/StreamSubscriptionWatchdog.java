package com.quantumbanking.infra.resilience;

import com.quantumbanking.infra.config.RedisStreamConfig;
import com.quantumbanking.infra.worker.CacheInvalidationWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StreamSubscriptionWatchdog {

    private final RedisStreamConfig redisStreamConfig;
    private final CacheInvalidationWorker worker;

    @Scheduled(fixedRateString = "${cache.watchdog.check-rate-ms}")
    public void checkAndRecoverSubscription() {

        if (redisStreamConfig.isSubscriptionActive()) return;

        log.warn("Subscription do stream de invalidação de cache está inativa. Tentando recriar...");

        try {
            redisStreamConfig.createConsumerGroup();
            redisStreamConfig.subscribe(worker);
        } catch (Exception e) {
            log.error("Falha ao tentar recriar a subscription. Nova tentativa no próximo ciclo.", e);
        }
    }
}