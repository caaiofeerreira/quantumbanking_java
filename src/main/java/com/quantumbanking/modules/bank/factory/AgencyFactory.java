package com.quantumbanking.modules.bank.factory;

import com.quantumbanking.modules.bank.domain.agency.Agency;
import com.quantumbanking.modules.bank.domain.bank.Bank;
import com.quantumbanking.modules.bank.dto.AgencyRegistrationDTO;
import com.quantumbanking.modules.shared.domain.address.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgencyFactory {

    public Agency createAgency(AgencyRegistrationDTO dto, String normalizedPhone, String normalizedCep, Bank bank) {

        Address address = new Address(
                dto.address().street(),
                dto.address().number(),
                dto.address().complement(),
                dto.address().neighborhood(),
                dto.address().city(),
                dto.address().state().toUpperCase(),
                normalizedCep
        );

        return new Agency(
                dto.agencyName(),
                dto.agencyNumber(),
                normalizedPhone,
                address,
                bank
        );
    }
}