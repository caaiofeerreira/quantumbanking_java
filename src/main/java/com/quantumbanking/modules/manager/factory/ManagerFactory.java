package com.quantumbanking.modules.manager.factory;

import com.quantumbanking.modules.manager.domain.Manager;
import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.dto.NormalizedUserData;
import org.springframework.stereotype.Component;

@Component
public class ManagerFactory {

    public Manager createManager(NormalizedUserData data, Long agencyId) {

        Address address = new Address(
                data.street(),
                data.number(),
                data.complement(),
                data.neighborhood(),
                data.city(),
                data.state(),
                data.zipCode()
        );

        return new Manager(
                data.name(),
                data.cpf(),
                data.phone(),
                data.email(),
                data.encryptedPassword(),
                address,
                agencyId
        );
    }
}