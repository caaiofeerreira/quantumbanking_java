package com.quantumbanking.modules.manager.domain;

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

    @Column(name = "agency_id", nullable = false)
    private Long agencyId;

    public Manager(String name, String cpf, String phone, String email,
                   String password, Address address, Long agencyId) {
        super(name, cpf, phone, email, password, UserRole.MANAGER, address);
        this.agencyId = agencyId;
    }
}