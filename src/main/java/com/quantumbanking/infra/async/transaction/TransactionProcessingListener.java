package com.quantumbanking.infra.async.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionProcessingListener implements StreamListener<String, MapRecord<String, String, String>> {

    private final TransactionStreamAckHandler transactionStreamAckHandler;

    @Override
    public void onMessage(MapRecord<String, String, String> record) {

        String transactionIdRaw = record.getValue().get("transactionId");
        transactionStreamAckHandler.process(transactionIdRaw, record);
    }
}
