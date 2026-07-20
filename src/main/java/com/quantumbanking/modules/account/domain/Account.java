package com.quantumbanking.modules.account.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.quantumbanking.infra.exception.TransactionNotAuthorizedException;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.client.domain.Client;

import com.quantumbanking.modules.pixKey.domain.PixKey;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Account")
@Table(name = "tb_account", indexes = {
        @Index(name = "idx_account_number", columnList = "number", unique = true)}
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", unique = true, nullable = false, length = 10)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(precision = 19, scale = 2)
    private BigDecimal balance;

    @Builder.Default
    @Column(name = "reserved_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal reservedBalance = BigDecimal.ZERO;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false)
    private Agency agency;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Builder.Default
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PixKey> pixKeys = new ArrayList<>();

    public BigDecimal getAvailableBalance() {
        return balance.subtract(reservedBalance);
    }

    public void ensureSufficientBalance(BigDecimal amount) {
        if (amount.compareTo(this.getAvailableBalance()) > 0) {
            throw new TransactionNotAuthorizedException(
                    "Saldo insuficiente. O valor total da operação excede o saldo disponível.");
        }
    }

    public void reserve(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionNotAuthorizedException("O valor da reserva deve ser positivo");
        }
        ensureSufficientBalance(amount);
        this.reservedBalance = this.reservedBalance.add(amount);
    }

    public void releaseReservation(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionNotAuthorizedException("O valor da liberação deve ser positivo");
        }
        this.reservedBalance = this.reservedBalance.subtract(amount);
    }

    public void confirmReservedDebit(BigDecimal amount) {
        this.releaseReservation(amount);
        this.balance = this.balance.subtract(amount);
    }

    public void debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionNotAuthorizedException("O valor do débito deve ser positivo");
        }
        ensureSufficientBalance(amount);
        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionNotAuthorizedException("O valor do crédito deve ser positivo");
        }
        this.balance = this.balance.add(amount);
    }
}