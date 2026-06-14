package com.quantumbanking.modules.account.repository;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.account.domain.AccountType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByAccountNumber(String accountNumber);

    boolean existsByClientIdAndType(Long clientId, AccountType type);

    Optional<Account> findByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("SELECT a FROM Account a WHERE a.agency.id = :agencyId")
    List<Account> findByAgencyId(@Param("agencyId") Long agencyId);

    @Query("SELECT a FROM Account a WHERE a.client.id = :clientId")
    List<Account> findByClientId(@Param("clientId") Long clientId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberWithLock(@Param("accountNumber") String accountNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdWithLock(@Param("id") Long id);
}
