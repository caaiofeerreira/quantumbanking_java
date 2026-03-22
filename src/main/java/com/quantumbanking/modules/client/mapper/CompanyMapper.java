package com.quantumbanking.modules.client.mapper;

import com.quantumbanking.modules.client.domain.Company;
import com.quantumbanking.modules.client.dto.CompanyResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyResponseDTO toResponseDTO(Company company) {
        return new CompanyResponseDTO(
                company.getCompanyName(),
                company.getTradeName(),
                company.getCnpj()
        );
    }
}