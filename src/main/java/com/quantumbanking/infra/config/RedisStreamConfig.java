package com.quantumbanking.infra.config;

import com.quantumbanking.infra.async.cache.CacheInvalidationPublisher;
import com.quantumbanking.infra.async.cache.CacheInvalidationListener;
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
public class RedisStreamConfig {

    private final StringRedisTemplate redisTemplate;
    private Subscription subscription;
    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> container;

    public static final String GROUP_NAME = "cache-invalidation-group";

    private final String consumerName = resolveConsumerName();

    private static String resolveConsumerName() {
        try {
            return "cache-listener-" + InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "cache-listener-" + UUID.randomUUID();
        }
    }

    @PostConstruct
    public void createConsumerGroup() {
        try {
            redisTemplate.opsForStream().createGroup(
                    CacheInvalidationPublisher.STREAM_KEY, GROUP_NAME);
        } catch (Exception e) {
            log.info("Consumer group '{}' já existe ou stream ainda não tem dados.", GROUP_NAME);
        }
    }

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(
            @Qualifier("redisConnectionFactory") RedisConnectionFactory connectionFactory,
            CacheInvalidationListener cacheInvalidationListener) {

        var options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                .pollTimeout(Duration.ofSeconds(2))
                .build();

        this.container = StreamMessageListenerContainer.create(connectionFactory, options);

        subscribe(cacheInvalidationListener);

        container.start();
        return container;
    }

    public void subscribe(CacheInvalidationListener cacheInvalidationListener) {
        this.subscription = container.receive(
                Consumer.from(GROUP_NAME, consumerName),
                StreamOffset.create(CacheInvalidationPublisher.STREAM_KEY, ReadOffset.lastConsumed()),
                cacheInvalidationListener
        );
        log.info("Subscription registrada no stream '{}' com consumer '{}'.", CacheInvalidationPublisher.STREAM_KEY, consumerName);
    }

    public boolean isSubscriptionActive() {
        return subscription != null && subscription.isActive();
    }
}