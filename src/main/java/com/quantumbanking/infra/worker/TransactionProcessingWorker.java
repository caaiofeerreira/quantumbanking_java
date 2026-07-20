package com.quantumbanking.infra.worker;

import com.quantumbanking.infra.config.TransactionProcessingStreamConfig;
import com.quantumbanking.modules.transaction.service.TransactionProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionProcessingWorker implements StreamListener<String, MapRecord<String, String, String>> {

    private final StringRedisTemplate redisTemplate;
    private final TransactionProcessingService transactionProcessingService;

    @Override
    public void onMessage(MapRecord<String, String, String> record) {

        String transactionIdRaw = record.getValue().get("transactionId");

        try {
            transactionProcessingService.processTransaction(UUID.fromString(transactionIdRaw));
        } catch (Exception e) {
            log.error("Erro ao processar transação (id={}) vinda do stream.", transactionIdRaw, e);
        }

        redisTemplate.opsForStream()
                .acknowledge(TransactionProcessingStreamConfig.GROUP_NAME, record);
    }
}
