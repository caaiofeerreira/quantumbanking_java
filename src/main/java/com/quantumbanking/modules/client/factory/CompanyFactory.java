package com.quantumbanking.modules.client.factory;

import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.Company;
import com.quantumbanking.modules.client.dto.CompanyRegistrationDTO;
import com.quantumbanking.modules.shared.domain.address.Address;
import com.quantumbanking.modules.shared.dto.AddressRequestDTO;
import com.quantumbanking.modules.shared.mapper.AddressMapper;
import com.quantumbanking.modules.shared.service.validation.CepValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyFactory {

    private final AddressMapper addressMapper;

    private final CepValidator cepValidator;

    public Company createCompany(CompanyRegistrationDTO registrationDTO, Client client) {

        String normalizedCep = cepValidator.normalizeCep(registrationDTO.address().zipCode());

        AddressRequestDTO addressRequestDTO = new AddressRequestDTO(
                registrationDTO.address().street(),
                registrationDTO.address().number(),
                registrationDTO.address().complement(),
                registrationDTO.address().neighborhood(),
                registrationDTO.address().city(),
                registrationDTO.address().state().toUpperCase(),
                normalizedCep
        );

        Address address = addressMapper.toAddress(addressRequestDTO);

        return new Company(registrationDTO, address, client);
    }

}