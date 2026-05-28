package com.quantumbanking.modules.bank.domain.bank;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "BankRegistry")
@Table(name = "tb_bank_registry")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(of = "id")
public class BankRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "compe", length = 3, unique = true, nullable = false)
    private String compe;

    @Column(name = "ispb", length = 8, unique = true, nullable = false)
    private String ispb;
}