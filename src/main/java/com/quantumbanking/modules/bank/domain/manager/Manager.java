package com.quantumbanking.modules.bank.domain.manager;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.dto.ManagerRegistrationDTO;
import com.quantumbanking.modules.shared.domain.user.User;
import com.quantumbanking.modules.shared.domain.user.UserRole;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Manager")
@Table(name = "tb_manager")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Manager extends User {

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false)
    private Agency agency;

    public Manager(ManagerRegistrationDTO dto, String encryptedPassword, Agency agency) {
        super(
                dto.name(),
                dto.cpf(),
                dto.phone(),
                dto.email(),
                encryptedPassword,
                UserRole.MANAGER,
                dto.address()
        );
        this.agency = agency;
    }
}