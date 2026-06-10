package com.quantumbanking.modules.client.mapper;

import com.quantumbanking.modules.client.domain.Company;
import com.quantumbanking.modules.client.dto.CompanyResponseDTO;
import com.quantumbanking.modules.shared.mapper.AddressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyMapper {

    private final AddressMapper addressMapper;

    public CompanyResponseDTO toCompanyResponseDTO(Company company) {
        return new CompanyResponseDTO(
                company.getCompanyName(),
                company.getTradeName(),
                company.getCnpj(),
                company.getStateRegistration(),
                addressMapper.toAddressDTO(company.getAddress())
        );
    }
}