package com.quantumbanking.modules.client.factory;

import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.dto.ClientRegistrationDTO;
import org.springframework.stereotype.Component;

@Component
public class ClientFactory {

    public Client createClient(ClientRegistrationDTO dto, String normalizedPhone, String normalizedEmail, String encryptedPassword) {
        return new Client(
                dto.name(),
                dto.cpf(),
                normalizedPhone,
                normalizedEmail,
                encryptedPassword,
                dto.address(),
                dto.clientType()
        );
    }
}