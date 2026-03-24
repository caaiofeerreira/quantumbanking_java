package com.quantumbanking.modules.account.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quantumbanking.infra.exception.TransactionNotAuthorizedException;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.client.domain.Client;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Entity(name = "Account")
@Table(name = "tb_account")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Account implements AccountOperations{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", unique = true, nullable = false, length = 10)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountType type;

    @Column(precision = 19, scale = 2)
    private BigDecimal balance;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, unique = true)
    private Client client;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false)
    private Agency agency;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PixKey> pixKeys;

    @JsonIgnore
    @Transient
    private final Lock lock = new ReentrantLock();

    @Override
    public void debit(BigDecimal amount) {

        lock.lock();
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new TransactionNotAuthorizedException("O valor deve ser positivo");
            }
            if (amount.compareTo(balance) > 0) {
                throw new TransactionNotAuthorizedException("Saldo insuficiente");
            }
            this.balance = this.balance.subtract(amount);

        } finally {
            lock.unlock();
        }
    }

    @Override
    public void credit(BigDecimal amount) {

        lock.lock();
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new TransactionNotAuthorizedException("O valor deve ser positivo");
            }
            this.balance = this.balance.add(amount);

        } finally {
            lock.unlock();
        }
    }
}