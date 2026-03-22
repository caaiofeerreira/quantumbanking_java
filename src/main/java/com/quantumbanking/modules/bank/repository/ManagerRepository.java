package com.quantumbanking.modules.bank.repository;

import com.quantumbanking.modules.bank.domain.manager.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
}