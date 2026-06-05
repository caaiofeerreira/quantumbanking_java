package com.quantumbanking.modules.bank.factory;

import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.domain.manager.Manager;
import com.quantumbanking.modules.bank.dto.ManagerRegistrationDTO;
import org.springframework.stereotype.Component;

@Component
public class ManagerFactory {

    public Manager createManager(ManagerRegistrationDTO dto, String normalizedPhone, String encryptedPassword, Agency agency) {
        return new Manager(
                dto.name(),
                dto.cpf(),
                normalizedPhone,
                dto.email(),
                encryptedPassword,
                dto.address(),
                agency
        );
    }
}
