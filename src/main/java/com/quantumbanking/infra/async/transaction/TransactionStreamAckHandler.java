package com.quantumbanking.infra.async.transaction;

import com.quantumbanking.infra.config.TransactionProcessingStreamConfig;
import com.quantumbanking.modules.transaction.service.TransactionProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionStreamAckHandler {

    private final StringRedisTemplate redisTemplate;
    private final TransactionProcessingService transactionProcessingService;

    public void process(String transactionIdRaw, MapRecord<String, ?, ?> record) {

        try {
            transactionProcessingService.processTransaction(UUID.fromString(transactionIdRaw));

            redisTemplate.opsForStream()
                    .acknowledge(TransactionProcessingStreamConfig.GROUP_NAME, record);

            log.info("Transação {} processada e confirmada no stream.", transactionIdRaw);

        } catch (Exception e) {
            log.error("Falha ao processar transação (id={}) vinda do stream. Mensagem NÃO " +
                    "confirmada, permanecerá pendente para reprocessamento.", transactionIdRaw, e);
        }
    }
}
