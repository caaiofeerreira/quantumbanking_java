package com.quantumbanking.modules.transaction.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.bank.domain.bank.BankAccount;
import com.quantumbanking.modules.loan.domain.Loan;
import com.quantumbanking.modules.pixKey.domain.PixKeyType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "Transaction")
@Table(name = "tb_transaction", indexes = {
        @Index(name = "idx_transaction_withdrawal", columnList = "origin_account_id, type, created_at")
})
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_account_id")
    private Account originAccount;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount;

    private String originName;
    private String originAccountNumber;
    private String originAgency;
    private String originBankCompe;
    private String originDocument;

    private String destinationName;
    private String destinationAccountNumber;
    private String destinationAgency;
    private String destinationBankCompe;
    private String destinationDocument;
    private String destinationBankName;

    private String pixKey;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "pix_key_type")
    private PixKeyType pixKeyType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id")
    private Loan loan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @Column(name = "failure_reason")
    private String failureReason;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now().withNano(0);
    }

    public boolean isSentBy(Long accountId) {
        return this.originAccount != null && this.originAccount.getId().equals(accountId);
    }

    public boolean isReceivedBy(Long accountId) {
        return this.destinationAccount != null && this.destinationAccount.getId().equals(accountId);
    }


    public void startProcessing() {
        if (this.status != TransactionStatus.PENDING) {
            throw new IllegalStateException(
                    "Não é possível iniciar processamento: transação " + id + " está em status " + status);
        }
        this.status = TransactionStatus.PROCESSING;
    }

    public void complete() {
        if (this.status != TransactionStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Não é possível completar: transação " + id + " está em status " + status);
        }
        this.status = TransactionStatus.COMPLETED;
    }

    public void fail(String reason) {
        if (this.status != TransactionStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Não é possível marcar como falha: transação " + id + " está em status " + status);
        }
        this.status = TransactionStatus.FAILED;
        this.failureReason = reason;
    }
}