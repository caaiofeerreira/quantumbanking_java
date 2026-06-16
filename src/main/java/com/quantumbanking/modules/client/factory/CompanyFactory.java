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

    public Company createCompany(CompanyRegistrationDTO dto, Client client) {

        String normalizedCep = cepValidator.normalizeCep(dto.address().zipCode());

        AddressRequestDTO normalizedAddress = new AddressRequestDTO(
                dto.address().street(),
                dto.address().number(),
                dto.address().complement(),
                dto.address().neighborhood(),
                dto.address().city(),
                dto.address().state().toUpperCase(),
                normalizedCep
        );

        Address address = addressMapper.toAddress(normalizedAddress);

        return new Company(dto, address, client);
    }
}