package com.quantumbanking.modules.transaction.repository;

import com.quantumbanking.modules.transaction.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.accountOrigin.id = :accountId OR t.accountDestiny.id = :accountId) " +
            "AND MONTH(t.createdAt) = :month " +
            "AND YEAR(t.createdAt) = :year " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findByAccountAndPeriod(@Param("accountId") Long accountId,
                                             @Param("month") int month,
                                             @Param("year") int year);
}
