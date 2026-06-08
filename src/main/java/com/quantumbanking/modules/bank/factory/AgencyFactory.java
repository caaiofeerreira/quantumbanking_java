package com.quantumbanking.modules.bank.factory;

import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.bank.dto.AgencyRegistrationDTO;
import com.quantumbanking.modules.bank.service.validation.AgencyValidator;
import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.service.validation.CepValidator;
import com.quantumbanking.modules.shared.service.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgencyFactory {

    private final CepValidator cepValidator;
    private final AgencyValidator agencyValidator;

    public Agency createAgency(AgencyRegistrationDTO dto, Bank bank) {

        String normalizedPhone = agencyValidator.normalizePhone(dto.phone());
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

        return new Agency(dto, normalizedPhone, normalizedAddress, bank);
    }
}