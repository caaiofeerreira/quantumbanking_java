package com.quantumbanking.modules.transaction.repository;

import com.quantumbanking.modules.transaction.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
