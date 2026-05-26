package com.quantumbanking.modules.bank.repository;

import com.quantumbanking.modules.bank.domain.bank.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long> {

    boolean existsByCompe(String bankCode);

    Optional<Bank> findByCompe(String compe);
}