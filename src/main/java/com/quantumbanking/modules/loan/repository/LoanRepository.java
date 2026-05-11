package com.quantumbanking.modules.loan.repository;

import com.quantumbanking.modules.loan.domain.Loan;
import com.quantumbanking.modules.loan.domain.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {

    @Query("SELECT l FROM Loan l JOIN l.account acc JOIN acc.agency ag WHERE ag.id = :agencyId AND l.status = :status")
    List<Loan> findByAgencyIdAndStatus(@Param("agencyId") Long agencyId, @Param("status") LoanStatus status);
}
