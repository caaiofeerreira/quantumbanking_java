package com.quantumbanking.modules.client.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.quantumbanking.modules.client.dto.CompanyRegistrationDTO;
import com.quantumbanking.modules.shared.domain.address.Address;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Company")
@Table(name = "tb_company")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Setter
    @Column(name = "trade_name", nullable = false)
    private String tradeName;

    @Column(unique = true, nullable = false)
    private String cnpj;

    @Column(name = "state_registration", nullable = false)
    private String stateRegistration;

    @Setter
    @Embedded
    private Address address;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    public Company(CompanyRegistrationDTO dto, Address address, Client client, String normalizedCnpj) {
        this.companyName = dto.companyName();
        this.tradeName = dto.tradeName();
        this.stateRegistration = dto.stateRegistration();
        this.address = address;
        this.client = client;
        this.cnpj = normalizedCnpj;
    }
}