package com.quantumbanking.infra.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionProcessingWorker implements StreamListener<String, MapRecord<String, String, String>> {

    private final TransactionStreamMessageProcessor transactionStream;

    @Override
    public void onMessage(MapRecord<String, String, String> record) {

        String transactionIdRaw = record.getValue().get("transactionId");
        transactionStream.process(transactionIdRaw, record);
    }
}
