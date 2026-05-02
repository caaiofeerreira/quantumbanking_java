package com.quantumbanking.modules.account.repository;

import com.quantumbanking.modules.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByAccountNumber(String accountNumber);

    Optional<Account> findByAccountNumber(@Param("accountNumber") String accountNumber);

    @Query("SELECT a FROM Account a WHERE a.agency.id = :agencyId")
    List<Account> findByAgencyId(@Param("agencyId") Long agencyId);

    @Query("SELECT a FROM Account a WHERE a.client.id = :userId")
    Optional<Account> findByUserId(@Param("userId") Long userId);
}
