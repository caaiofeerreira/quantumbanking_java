package com.quantumbanking.modules.bank.domain.bank;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Bank")
@Table(name = "tb_bank")
@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "bank_code", length = 3, unique = true, nullable = false)
    private String bankCode;
}