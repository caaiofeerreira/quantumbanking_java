package com.quantumbanking.modules.loan.domain;

import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.manager.domain.Manager;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "Loan")
@Table(name = "tb_loan")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Loan  {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "interest_rate", precision = 19, scale = 2, nullable = false)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private Integer installments;

    @Column
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @Column(name = "paid_installments")
    private Integer paidInstallments;

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "installment_amount", precision = 19, scale = 2)
    private BigDecimal installmentAmount;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Manager manager;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = LoanStatus.REQUESTED;
        this.paidInstallments = 0;
    }

    public Loan(Account account, BigDecimal amount, BigDecimal interestRate,
                Integer installments, String description,
                BigDecimal installmentAmount, BigDecimal totalAmount) {
        this.account = account;
        this.amount = amount;
        this.interestRate = interestRate;
        this.installments = installments;
        this.description = description;
        this.installmentAmount = installmentAmount;
        this.totalAmount = totalAmount;
    }

    public void approveLoan(Manager manager) {
        this.manager = manager;
        this.status = LoanStatus.APPROVED;
        this.startDate = LocalDate.now();
        this.endDate = LocalDate.now().plusMonths(this.installments);
    }

    public void rejectLoan(Manager manager) {
        this.manager = manager;
        this.status = LoanStatus.REJECTED;
    }
}