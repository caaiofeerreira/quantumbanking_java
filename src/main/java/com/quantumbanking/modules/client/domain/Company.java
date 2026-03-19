package com.quantumbanking.modules.client.domain;

import com.quantumbanking.modules.client.dto.CompanyRegistrationDTO;
import com.quantumbanking.modules.shared.domain.address.Address;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Company")
@Table(name = "tb_company")
@Getter
@NoArgsConstructor
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

    @OneToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    public Company(CompanyRegistrationDTO dto, Client client) {
        this.companyName = dto.companyName();
        this.tradeName = dto.tradeName();
        this.cnpj = dto.cnpj();
        this.stateRegistration = dto.stateRegistration();
        this.address = dto.address();
        this.client = client;
    }
}