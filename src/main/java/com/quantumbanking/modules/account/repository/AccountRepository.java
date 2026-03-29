package com.quantumbanking.modules.account.repository;

import com.quantumbanking.modules.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByAccountNumber(String accountNumber);

    Optional<Account> findByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("SELECT a.balance FROM Account a WHERE a.client.id = :clientId")
    Optional<BigDecimal> findBalanceByClientId(@Param("clientId") Long clientId);

    @Query("SELECT a FROM Account a WHERE a.client.id = :clientId")
    Optional<Account> findByClientId(@Param("clientId") Long clientId);
}
