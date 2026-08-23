package com.quantumbanking.modules.transaction.repository;

import com.quantumbanking.modules.transaction.domain.OutboxStatus;
import com.quantumbanking.modules.transaction.domain.TransactionOutbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionOutboxRepository extends JpaRepository<TransactionOutbox, Long> {

    List<TransactionOutbox> findByStatusAndRetryCountLessThanOrderByCreatedAtAsc(OutboxStatus status, int retryCount);
}