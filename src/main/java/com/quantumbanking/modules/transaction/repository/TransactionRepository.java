package com.quantumbanking.modules.transaction.repository;

import com.quantumbanking.modules.transaction.domain.Transaction;
import com.quantumbanking.modules.transaction.domain.TransactionStatus;
import com.quantumbanking.modules.transaction.domain.TransactionType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.originAccount.id = :accountId OR t.destinationAccount.id = :accountId) " +
            "AND MONTH(t.createdAt) = :month " +
            "AND YEAR(t.createdAt) = :year " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findByAccountAndPeriod(@Param("accountId") Long accountId,
                                             @Param("month") int month,
                                             @Param("year") int year);

    @Query("""
    SELECT COUNT(t) FROM Transaction t
    WHERE t.originAccount.id = :accountId
    AND t.type = :type
    AND t.createdAt >= :start
    AND t.createdAt < :end
""")
    long countByOriginAccountAndTypeAndPeriod(
            @Param("accountId") Long accountId,
            @Param("type") TransactionType type,
            @Param("start") Instant start,
            @Param("end") Instant end
    );

    @Query("""
    SELECT t FROM Transaction t
    WHERE t.id = :transactionId
    AND (t.originAccount.id = :accountId OR t.destinationAccount.id = :accountId)
    """)
    Optional<Transaction> findByIdAndAccountInvolved(@Param("transactionId") UUID transactionId,
                                                     @Param("accountId") Long accountId);

    List<Transaction> findByTypeAndStatusAndAvailableAtLessThanEqualOrderByCreatedAtAsc(
            TransactionType type,
            TransactionStatus status,
            Instant now
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Transaction t where t.id = :id")
    Optional<Transaction> findByIdWithLock(UUID id);
}
