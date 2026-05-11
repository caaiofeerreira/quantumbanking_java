package com.quantumbanking.modules.account.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.quantumbanking.infra.exception.TransactionNotAuthorizedException;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.client.domain.Client;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Account")
@Table(name = "tb_account")
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

    @Builder.Default
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PixKey> pixKeys = new ArrayList<>();

    public void debit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionNotAuthorizedException("O valor do débito deve ser positivo");
        }

        if (amount.compareTo(this.balance) > 0) {
            throw new TransactionNotAuthorizedException("Saldo insuficiente para realizar a operação");
        }

        this.balance = this.balance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionNotAuthorizedException("O valor do crédito deve ser positivo");
        }

        this.balance = this.balance.add(amount);
    }
}