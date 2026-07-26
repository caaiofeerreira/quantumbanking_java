package com.quantumbanking.infra.worker;

import com.quantumbanking.infra.config.TransactionProcessingStreamConfig;
import com.quantumbanking.infra.listener.TransactionOutboxPublisher;
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
public class TransactionProcessingReprocessor {

    private final StringRedisTemplate redisTemplate;
    private final TransactionStreamMessageProcessor transactionStream;

    private static final String CONSUMER_NAME = resolveConsumerName();

    @Value("${transaction.reprocessor.minimum-pending-time}")
    private Duration minimumPendingTime;

    @Value("${transaction.reprocessor.batch-size}")
    private int batchSize;

    @Scheduled(fixedRateString = "${transaction.reprocessor.fixed-rate-ms}")
    public void reprocessPendingMessages() {

        PendingMessages pendingMessages = redisTemplate.opsForStream().pending(
                TransactionOutboxPublisher.STREAM_KEY,
                Consumer.from(TransactionProcessingStreamConfig.GROUP_NAME, CONSUMER_NAME),
                Range.unbounded(),
                batchSize
        );

        if (pendingMessages.isEmpty()) return;

        List<String> idsToClaim = pendingMessages.stream()
                .filter(msg -> msg.getElapsedTimeSinceLastDelivery().compareTo(minimumPendingTime) >= 0)
                .map(PendingMessage::getIdAsString)
                .toList();

        if (idsToClaim.isEmpty()) return;

        log.info("Reivindicando {} transação(ões) pendente(s).", idsToClaim.size());

        List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream().claim(
                TransactionOutboxPublisher.STREAM_KEY,
                TransactionProcessingStreamConfig.GROUP_NAME,
                CONSUMER_NAME,
                RedisStreamCommands.XClaimOptions.minIdle(minimumPendingTime).ids(idsToClaim)
        );

        for (MapRecord<String, Object, Object> record : messages) {
            String transactionIdRaw = (String) record.getValue().get("transactionId");
            transactionStream.process(transactionIdRaw, record);
        }
    }

    private static String resolveConsumerName() {
        try {
            return "transaction-worker-" + InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "transaction-worker-" + UUID.randomUUID();
        }
    }
}