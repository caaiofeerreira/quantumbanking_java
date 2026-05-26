package com.quantumbanking.modules.bank.domain.bank;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Bank")
@Table(name = "tb_bank")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "compe", length = 3, unique = true, nullable = false)
    private String compe;

    @Column(name = "ispb", length = 8, unique = true)
    private String ispb;

    @Column(name = "cnpj", length = 14, unique = true)
    private String cnpj;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_account_id")
    private BankAccount account;
}