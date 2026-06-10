package com.quantumbanking.modules.client.factory;

import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.dto.ClientRegistrationDTO;
import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.dto.AddressRequestDTO;
import com.quantumbanking.modules.shared.mapper.AddressMapper;
import com.quantumbanking.modules.shared.service.validation.CepValidator;
import com.quantumbanking.modules.shared.service.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClientFactory {

    private final AddressMapper addressMapper;

    private final UserValidator userValidator;
    private final CepValidator cepValidator;

    public Client createClient(ClientRegistrationDTO dto, String encryptedPassword) {

        String normalizedCpf = userValidator.normalizeCpf(dto.cpf());
        String normalizedPhone = userValidator.normalizePhone(dto.phone());
        String normalizedEmail = userValidator.normalizeEmail(dto.email());
        String normalizedCep = cepValidator.normalizeCep(dto.address().zipCode());

        AddressRequestDTO addressRequestDTO = new AddressRequestDTO(
                dto.address().street(),
                dto.address().number(),
                dto.address().complement(),
                dto.address().neighborhood(),
                dto.address().city(),
                dto.address().state().toUpperCase(),
                normalizedCep
        );

        Address address = addressMapper.toAddress(addressRequestDTO);

        return new Client(
                dto.name(),
                normalizedCpf,
                normalizedPhone,
                normalizedEmail,
                encryptedPassword,
                address,
                dto.clientType()
        );
    }
}