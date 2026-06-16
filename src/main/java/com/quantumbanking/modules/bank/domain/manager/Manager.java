package com.quantumbanking.modules.bank.domain.manager;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.shared.domain.user.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Manager")
@Table(name = "tb_manager")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Manager extends User {

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false)
    private Agency agency;

    public Manager(String name, String cpf, String phone, String email,
                   String password, Address address, Agency agency) {
        super(name, cpf, phone, email, password, UserRole.MANAGER, address);
        this.agency = agency;
    }
}