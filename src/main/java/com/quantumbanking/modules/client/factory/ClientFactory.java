package com.quantumbanking.modules.client.factory;

import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.dto.NormalizedUserData;
import org.springframework.stereotype.Component;

@Component
public class ClientFactory {

    public Client createClient(NormalizedUserData data, ClientType clientType) {

        Address address = new Address(
                data.street(),
                data.number(),
                data.complement(),
                data.neighborhood(),
                data.city(),
                data.state(),
                data.zipCode()
        );

        return new Client(
                data.name(),
                data.cpf(),
                data.phone(),
                data.email(),
                data.encryptedPassword(),
                address,
                clientType
        );
    }
}