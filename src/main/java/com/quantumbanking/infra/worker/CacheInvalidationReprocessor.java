package com.quantumbanking.infra.worker;

import com.quantumbanking.infra.config.RedisStreamConfig;
import com.quantumbanking.infra.listener.CacheInvalidationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheInvalidationReprocessor {

    private final StringRedisTemplate redisTemplate;
    private final CacheInvalidationWorker worker;

    private static final String CONSUMER_NAME = "worker-instance-1";
    private static final Duration MINIMUM_PENDING_TIME = Duration.ofMinutes(5);

    @Scheduled(fixedRate = 60_000)
    public void reprocessPendingMessages() {

        PendingMessages pendingMessages = redisTemplate.opsForStream().pending(
                CacheInvalidationPublisher.STREAM_KEY,
                Consumer.from(RedisStreamConfig.GROUP_NAME, CONSUMER_NAME),
                Range.unbounded(),
                100
        );

        if (pendingMessages.isEmpty()) return;

        List<String> idsToClaim = pendingMessages.stream()
                .filter(msg -> msg.getElapsedTimeSinceLastDelivery().compareTo(MINIMUM_PENDING_TIME) >= 0)
                .map(PendingMessage::getIdAsString)
                .toList();

        if (idsToClaim.isEmpty()) return;

        log.info("Reivindicando {} mensagem(ns) pendente(s).", idsToClaim.size());

        List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream().claim(
                CacheInvalidationPublisher.STREAM_KEY,
                RedisStreamConfig.GROUP_NAME,
                CONSUMER_NAME,
                RedisStreamCommands.XClaimOptions.minIdle(MINIMUM_PENDING_TIME).ids(idsToClaim)
        );

        for (MapRecord<String, Object, Object> record : messages) {
            try {
                String accounts = (String) record.getValue().get("accounts");
                worker.processInvalidation(accounts);

                redisTemplate.opsForStream()
                        .acknowledge(RedisStreamConfig.GROUP_NAME, record);

                log.info("Mensagem pendente reprocessada com sucesso. ID: {}", record.getId());
            } catch (Exception e) {
                log.error("Falha ao reprocessar mensagem pendente. ID: {}", record.getId(), e);
            }
        }
    }

}
