package com.quantumbanking.infra.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenRedisService {

    private static final String PREFIX = "token_ativo:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${api.security.token.expiration}")
    private long expiration;

    private Duration ttl() {
        return Duration.ofMinutes(expiration);
    }

    public void saveActiveToken(Long userId, String token) {
        redisTemplate.opsForValue().set(PREFIX + userId, token, ttl());
    }

    public boolean checkActiveToken(Long userId, String token) {
        Object stored = redisTemplate.opsForValue().get(PREFIX + userId);
        return token.equals(stored);
    }
}