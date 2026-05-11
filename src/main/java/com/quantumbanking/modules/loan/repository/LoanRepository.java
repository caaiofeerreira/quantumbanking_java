package com.quantumbanking.modules.loan.repository;

import com.quantumbanking.modules.loan.domain.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
}
