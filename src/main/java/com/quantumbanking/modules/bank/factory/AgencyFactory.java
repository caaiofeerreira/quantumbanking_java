package com.quantumbanking.modules.bank.factory;

import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.bank.dto.AgencyRegistrationDTO;
import com.quantumbanking.modules.bank.service.validation.AgencyValidator;
import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.dto.AddressRequestDTO;
import com.quantumbanking.modules.shared.mapper.AddressMapper;
import com.quantumbanking.modules.shared.service.validation.CepValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgencyFactory {

    private final AddressMapper addressMapper;

    private final CepValidator cepValidator;
    private final AgencyValidator agencyValidator;

    public Agency createAgency(AgencyRegistrationDTO dto, Bank bank) {

        String normalizedPhone = agencyValidator.normalizePhone(dto.phone());
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

        return new Agency(dto, normalizedPhone, address, bank);
    }
}