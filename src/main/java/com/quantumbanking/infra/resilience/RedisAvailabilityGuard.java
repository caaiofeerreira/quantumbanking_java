package com.quantumbanking.infra.resilience;

import com.quantumbanking.infra.exception.RedisUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RedisAvailabilityGuard {

    private final RedisConnectionFactory healthCheckConnectionFactory;

    public RedisAvailabilityGuard(@Qualifier("healthCheckConnectionFactory") RedisConnectionFactory healthCheckConnectionFactory) {
        this.healthCheckConnectionFactory = healthCheckConnectionFactory;
    }

    public void ensureAvailable() {

        try(RedisConnection connection = healthCheckConnectionFactory.getConnection()) {

            String response = connection.ping();
            if (!"PONG".equalsIgnoreCase(response)) throw new RedisUnavailableException("Redis não respondeu corretamente ao PING.");
        } catch (Exception e) {
            log.error("Redis indisponível. Movimentação bloqueada por segurança. Motivo: {}", e.getMessage());
            throw new RedisUnavailableException("Não foi possível processar sua movimentação no momento. Tente novamente em instantes.", e);
        }
    }

}
