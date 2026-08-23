package com.quantumbanking.infra.config;

import com.quantumbanking.infra.async.transaction.TransactionOutboxPublisher;
import com.quantumbanking.infra.async.transaction.TransactionProcessingListener;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TransactionProcessingStreamConfig {

    private final StringRedisTemplate redisTemplate;
    public static final String GROUP_NAME = "transaction-processing-group";

    private final String consumerName = resolveConsumerName();

    private Subscription subscription;
    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> container;

    private static String resolveConsumerName() {
        try {
            return "transaction-listener-" + InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "transaction-listener-" + UUID.randomUUID();
        }
    }

    @PostConstruct
    public void createConsumerGroup() {
        try {
            redisTemplate.opsForStream().createGroup(
                    TransactionOutboxPublisher.STREAM_KEY, GROUP_NAME);
        } catch (Exception e) {
            log.info("Consumer group '{}' já existe ou stream ainda não tem dados.", GROUP_NAME);
        }
    }

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> transactionProcessingListenerContainer(
            @Qualifier("redisConnectionFactory") RedisConnectionFactory connectionFactory,
            TransactionProcessingListener transactionProcessingListener) {

        var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(2))
                .build();

        this.container = StreamMessageListenerContainer.create(connectionFactory, options);

        subscribe(transactionProcessingListener);

        container.start();
        return container;
    }

    public void subscribe(TransactionProcessingListener transactionProcessingListener) {
        this.subscription = container.receive(
                Consumer.from(GROUP_NAME, consumerName),
                StreamOffset.create(TransactionOutboxPublisher.STREAM_KEY, ReadOffset.lastConsumed()),
                transactionProcessingListener
        );
        log.info("Subscription registrada no stream '{}' com consumer '{}'.", TransactionOutboxPublisher.STREAM_KEY, consumerName);
    }

    public boolean isSubscriptionActive() {
        return subscription != null && subscription.isActive();
    }
}