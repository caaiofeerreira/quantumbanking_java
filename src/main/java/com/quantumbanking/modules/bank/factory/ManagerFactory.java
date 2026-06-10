package com.quantumbanking.modules.bank.factory;

import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.domain.manager.Manager;
import com.quantumbanking.modules.bank.dto.ManagerRegistrationDTO;
import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.dto.AddressRequestDTO;
import com.quantumbanking.modules.shared.mapper.AddressMapper;
import com.quantumbanking.modules.shared.service.validation.CepValidator;
import com.quantumbanking.modules.shared.service.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ManagerFactory {

    private final AddressMapper addressMapper;

    private final UserValidator userValidator;
    private final CepValidator cepValidator;

    public Manager createManager(ManagerRegistrationDTO dto, String encryptedPassword, Agency agency) {

        String normalizedCpf = userValidator.normalizeCpf(dto.cpf());
        String normalizedPhone = userValidator.normalizePhone(dto.phone());
        String normalizedEmail = userValidator.normalizeEmail(dto.email());
        String normalizedCep = cepValidator.normalizeCep(dto.address().getZipCode());

        AddressRequestDTO addressRequestDTO = new AddressRequestDTO(
                dto.address().getStreet(),
                dto.address().getNumber(),
                dto.address().getComplement(),
                dto.address().getNeighborhood(),
                dto.address().getCity(),
                dto.address().getState().toUpperCase(),
                normalizedCep
        );

        Address address = addressMapper.toAddress(addressRequestDTO);

        return new Manager(
                dto.name(),
                normalizedCpf,
                normalizedPhone,
                normalizedEmail,
                encryptedPassword,
                address,
                agency
        );
    }
}
