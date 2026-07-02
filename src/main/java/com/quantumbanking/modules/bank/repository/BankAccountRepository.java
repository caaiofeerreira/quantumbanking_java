package com.quantumbanking.modules.bank.repository;

import com.quantumbanking.modules.bank.domain.bank.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
}