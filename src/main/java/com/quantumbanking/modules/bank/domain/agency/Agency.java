package com.quantumbanking.modules.bank.domain.agency;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.quantumbanking.modules.account.domain.Account;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.bank.domain.manager.Manager;
import com.quantumbanking.modules.bank.dto.AgencyRegistrationDTO;
import com.quantumbanking.modules.shared.domain.address.Address;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "Agency")
@Table(name = "tb_agency")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "agency_name", nullable = false)
    private String agencyName;

    @Column(name = "agency_number", nullable = false)
    private String agencyNumber;

    @Setter
    @Column(name = "phone", nullable = false)
    private String phone;

    @Setter
    @Embedded
    private Address address;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agency")
    private List<Manager> managers;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "agency")
    private List<Account> accountList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banking_id", nullable = false)
    private Bank bank;

    public Agency(AgencyRegistrationDTO agencyDTO, Bank bank) {
        this.agencyName = agencyDTO.agencyName();
        this.agencyNumber = agencyDTO.agencyNumber();
        this.phone = agencyDTO.phone();
        this.address = agencyDTO.address();
        this.bank = bank;
    }
}