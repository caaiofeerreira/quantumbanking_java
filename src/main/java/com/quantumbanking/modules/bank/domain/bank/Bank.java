package com.quantumbanking.modules.bank.domain.bank;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Bank")
@Table(name = "tb_bank")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "bank_code", length = 3, unique = true, nullable = false)
    private String bankCode;

    @Column(name = "ispb", length = 8, unique = true)
    private String ispb;

    public Bank(String name, String bankCode, String ispb) {
        this.name= name;
        this.bankCode = bankCode;
        this.ispb = ispb;
    }
}