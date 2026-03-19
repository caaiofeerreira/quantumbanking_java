package com.quantumbanking.modules.account.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "PixKey")
@Table(name = "tb_pix_key")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class PixKey {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "pix_key", unique = true, nullable = false)
    private String key;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PixKeyType type;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    public PixKey(String key, PixKeyType type, Account account) {
        this.key = key;
        this.type = type;
        this.account = account;
    }
}