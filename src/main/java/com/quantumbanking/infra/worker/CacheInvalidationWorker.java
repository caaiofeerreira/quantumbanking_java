package com.quantumbanking.infra.worker;

import com.quantumbanking.infra.config.RedisStreamConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheInvalidationWorker implements StreamListener<String, MapRecord<String, String, String>> {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void onMessage(MapRecord<String, String, String> record) {

        String message = record.getValue().get("accounts");
        processInvalidation(message);

        redisTemplate.opsForStream().acknowledge(RedisStreamConfig.GROUP_NAME, record);
    }

    public void processInvalidation(String message) {

        try {
            log.info("Worker iniciou o processamento da mensagem: {}", message);

            String[] accountArray = message.split(",");

            for (String accountNumber : accountArray) {
                String trimmedAccount = accountNumber.trim();

                if (!trimmedAccount.isEmpty()) {
                    clearCacheByPattern("statement:" + trimmedAccount + ":*");
                    clearCacheKey("balance::" + trimmedAccount);
                }
            }
            log.info("Worker finalizou a invalidação de cache com sucesso.");
        } catch (Exception e) {
            log.error("Erro no processamento do Worker para a mensagem: {}", message, e);
            throw e;
        }
    }

    private void clearCacheByPattern(String pattern) {

        Set<String> keys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keysFound = new HashSet<>();

            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(pattern).count(100).build())) {
                while (cursor.hasNext()) {
                    keysFound.add(new String(cursor.next()));
                }
            } catch (Exception e) {
                log.error("Erro ao escanear chaves no Redis com o padrão: '{}'", pattern, e);
            }
            return keysFound;
        });

        if (keys != null && !keys.isEmpty()) redisTemplate.delete(keys);

    }

    private void clearCacheKey(String key) {
        redisTemplate.delete(key);
    }
}