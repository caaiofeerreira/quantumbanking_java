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
    @JoinColumn(name = "account_origin_id")
    private Account accountOrigin;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_destiny_id")
    private Account accountDestiny;

    private String originName;
    private String destinyName;
    private String destinyAccount;
    private String destinyAgency;
    private String bankCode;
    private String destinyDocument;
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
        return this.accountOrigin != null && this.accountOrigin.getId().equals(accountId);
    }

    public boolean isReceivedBy(Long accountId) {
        return this.accountDestiny != null && this.accountDestiny.getId().equals(accountId);
    }

}