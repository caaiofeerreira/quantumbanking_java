package com.quantumbanking.modules.client.service;

import com.quantumbanking.modules.client.domain.Client;
import com.quantumbanking.modules.client.domain.ClientType;
import com.quantumbanking.modules.client.domain.Company;
import com.quantumbanking.modules.client.dto.ClientRegistrationDTO;
import com.quantumbanking.modules.client.factory.CompanyFactory;
import com.quantumbanking.modules.client.repository.CompanyRepository;
import com.quantumbanking.modules.client.service.validator.CompanyValidator;
import com.quantumbanking.modules.shared.service.validation.CepValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyFactory companyFactory;
    private final CompanyRepository companyRepository;
    private final CompanyValidator companyValidator;

    public Optional<Company> findByClient(Client client) {
        return companyRepository.findByClient(client);
    }

    @Transactional
    public Company createIfApplicable(ClientRegistrationDTO dto, Client client) {

        companyValidator.checkCompanyDataConsistency(dto.clientType(), dto.company());

        if (dto.clientType() != ClientType.JURIDICA) return null;

        Company company = companyFactory.createCompany(dto.company(), client);
        companyRepository.save(company);
        return company;
    }
}