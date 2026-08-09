package com.quantumbanking.infra.worker;

import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.domain.TransactionStatus;
import com.quantumbanking.modules.transaction.domain.TransactionType;
import com.quantumbanking.modules.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WithdrawalReprocessor {

    private final TransactionRepository transactionRepository;
    private final WithdrawalReprocessorWorker worker;

    @Scheduled(fixedRateString = "${transaction.withdrawal-reprocessor.fixed-rate-ms}")
    public void reprocessPendingWithdrawals() {

        List<Transaction> pending = transactionRepository
                .findByTypeAndStatusAndAvailableAtLessThanEqualOrderByCreatedAtAsc(
                        TransactionType.WITHDRAWAL,
                        TransactionStatus.PENDING,
                        Instant.now()
                );

        for (Transaction transaction : pending) {
            try {
                worker.processOne(transaction.getId());
            } catch (Exception e) {
                log.error("Falha ao reprocessar saque pendente, transactionId={}", transaction.getId(), e);
            }
        }
    }
}
