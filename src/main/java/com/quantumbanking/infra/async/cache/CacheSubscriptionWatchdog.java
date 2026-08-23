package com.quantumbanking.infra.async.cache;

import com.quantumbanking.infra.config.RedisStreamConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheSubscriptionWatchdog {

    private final RedisStreamConfig redisStreamConfig;
    private final CacheInvalidationListener cacheInvalidationListener;

    @Scheduled(fixedRateString = "${cache.watchdog.check-rate-ms}")
    public void checkAndRecoverSubscription() {

        if (redisStreamConfig.isSubscriptionActive()) return;

        log.warn("Subscription do stream de invalidação de cache está inativa. Tentando recriar...");

        try {
            redisStreamConfig.createConsumerGroup();
            redisStreamConfig.subscribe(cacheInvalidationListener);
            log.info("Subscription recriada com sucesso. Listener voltou a processar invalidações de cache.");
        } catch (Exception e) {
            log.error("Falha ao tentar recriar a subscription. Nova tentativa no próximo ciclo.", e);
        }
    }
}