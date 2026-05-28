package com.quantumbanking.modules.bank.repository;

import com.quantumbanking.modules.bank.domain.bank.BankRegistry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankRegistryRepository extends JpaRepository<BankRegistry, Long> {

    Optional<BankRegistry> findByCompe(String compe);

}