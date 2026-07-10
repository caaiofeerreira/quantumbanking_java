package com.quantumbanking.infra.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class TokenRedisService {

    private static final String PREFIX = "token_ativo:";

    @Qualifier("fastFailRedisTemplate")
    private final StringRedisTemplate redisTemplate;

    @Value("${api.security.token.expiration}")
    private long expiration;

    public TokenRedisService(@Qualifier("fastFailRedisTemplate") StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private Duration ttl() {
        return Duration.ofMinutes(expiration);
    }

    public void saveActiveToken(Long userId, String jti) {

        try {
            redisTemplate.opsForValue().set(PREFIX + userId, jti, ttl());
        } catch (Exception e) {
            log.error("Redis indisponível ao salvar JTI ativo para o usuário {}. " +
                    "Login prossegue sem registro de sessão revogável. Motivo: {}", userId, e.getMessage());
        }
    }

    public boolean checkActiveToken(Long userId, String jti) {

        try {
            String stored = redisTemplate.opsForValue().get(PREFIX + userId);
            return jti.equals(stored);
        } catch (Exception e) {
            log.error("Redis indisponível ao validar JTI para o usuário {}. " +
                    "Permitindo acesso em modo degradado (Stateless). Motivo: {}", userId, e.getMessage());
        }
        return true;
    }
}