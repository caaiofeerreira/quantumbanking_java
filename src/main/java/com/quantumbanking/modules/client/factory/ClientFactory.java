package com.quantumbanking.modules.client.factory;

import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.dto.ClientRegistrationDTO;
import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.service.validation.CepValidator;
import com.quantumbanking.modules.shared.service.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientFactory {

    private final UserValidator userValidator;
    private final CepValidator cepValidator;

    public Client createClient(ClientRegistrationDTO dto, String encryptedPassword) {

        String normalizedPhone = userValidator.normalizePhone(dto.phone());
        String normalizedEmail = userValidator.normalizeEmail(dto.email());
        String normalizedCep = cepValidator.normalizeCep(dto.address().getZipCode());

        Address normalizedAddress = new Address(
                dto.address().getStreet(),
                dto.address().getNumber(),
                dto.address().getComplement(),
                dto.address().getNeighborhood(),
                dto.address().getCity(),
                dto.address().getState().toUpperCase(),
                normalizedCep
        );

        return new Client(
                dto.name(),
                dto.cpf(),
                normalizedPhone,
                normalizedEmail,
                encryptedPassword,
                normalizedAddress,
                dto.clientType()
        );
    }
}