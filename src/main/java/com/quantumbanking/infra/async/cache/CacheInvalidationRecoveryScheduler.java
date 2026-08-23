package com.quantumbanking.infra.async.cache;

import com.quantumbanking.infra.config.RedisStreamConfig;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheInvalidationRecoveryScheduler {

    private final StringRedisTemplate redisTemplate;
    private final CacheInvalidationListener cacheInvalidationListener;

    private static final String CONSUMER_NAME = resolveConsumerName();

    @Value("${cache.stream-recovery.minimum-pending-time}")
    private Duration minimumPendingTime;

    @Value("${cache.stream-recovery.batch-size}")
    private int batchSize;

    private static String resolveConsumerName() {
        try {
            return "cache-recovery-" + InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "cache-recovery-" + UUID.randomUUID();
        }
    }

    @Scheduled(fixedRateString = "${cache.stream-recovery.fixed-rate-ms}")
    public void recoverPendingMessages() {

        PendingMessages pendingMessages = redisTemplate.opsForStream().pending(
                CacheInvalidationPublisher.STREAM_KEY,
                Consumer.from(RedisStreamConfig.GROUP_NAME, CONSUMER_NAME),
                Range.unbounded(),
                batchSize
        );

        if (pendingMessages.isEmpty()) return;

        List<String> idsToClaim = pendingMessages.stream()
                .filter(msg -> msg.getElapsedTimeSinceLastDelivery().compareTo(minimumPendingTime) >= 0)
                .map(PendingMessage::getIdAsString)
                .toList();

        if (idsToClaim.isEmpty()) return;

        log.info("Reivindicando {} mensagem(ns) pendente(s).", idsToClaim.size());

        List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream().claim(
                CacheInvalidationPublisher.STREAM_KEY,
                RedisStreamConfig.GROUP_NAME,
                CONSUMER_NAME,
                RedisStreamCommands.XClaimOptions.minIdle(minimumPendingTime).ids(idsToClaim)
        );

        for (MapRecord<String, Object, Object> record : messages) {
            try {
                String accounts = (String) record.getValue().get("accounts");
                cacheInvalidationListener.processInvalidation(accounts);

                redisTemplate.opsForStream()
                        .acknowledge(RedisStreamConfig.GROUP_NAME, record);

                log.info("Mensagem pendente reprocessada com sucesso. ID: {}", record.getId());
            } catch (Exception e) {
                log.error("Falha ao reprocessar mensagem pendente. ID: {}", record.getId(), e);
            }
        }
    }
}