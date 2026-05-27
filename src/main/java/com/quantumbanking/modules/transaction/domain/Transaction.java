package com.quantumbanking.modules.transaction.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quantumbanking.modules.account.domain.Account;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "Transaction")
@Table(name = "tb_transaction")
@Getter
@Builder
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

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isSentBy(Long accountId) {
        return this.originAccount != null && this.originAccount.getId().equals(accountId);
    }

    public boolean isReceivedBy(Long accountId) {
        return this.destinationAccount != null && this.destinationAccount.getId().equals(accountId);
    }

}